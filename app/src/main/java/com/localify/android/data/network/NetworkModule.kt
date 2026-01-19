package com.localify.android.data.network

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Route
import okhttp3.Response
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    internal const val BASE_URL = "https://staging.localify.org/"
    private const val AUTH_PREFS_NAME = "localify_auth"
    internal const val KEY_ACCESS_TOKEN = "access_token"
    internal const val KEY_REFRESH_TOKEN = "refresh_token"
    internal const val KEY_EXPIRES_AT_MS = "expires_at_ms"

    @Volatile
    private var initialized: Boolean = false

    private lateinit var authPrefs: SharedPreferences
    private lateinit var tokenStore: AuthTokenStore
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var refreshClient: OkHttpClient
    private lateinit var retrofit: Retrofit
    private lateinit var appContext: Context

    fun init(context: Context) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return

            appContext = context.applicationContext
            authPrefs = context.applicationContext.getSharedPreferences(AUTH_PREFS_NAME, Context.MODE_PRIVATE)
            tokenStore = AuthTokenStore(authPrefs)

            refreshClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val cacheDir = File(appContext.cacheDir, "http_cache")
            val cacheSizeBytes = 10L * 1024L * 1024L
            val cache = Cache(cacheDir, cacheSizeBytes)

            okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(offlineCacheRequestInterceptor)
                .addInterceptor(AuthHeaderInterceptor(tokenStore))
                .authenticator(TokenRefreshAuthenticator(tokenStore, refreshClient))
                .addNetworkInterceptor(cacheControlResponseInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            initialized = true
        }
    }

    private fun ensureInitialized() {
        check(initialized) { "NetworkModule.init(context) must be called before using NetworkModule.apiService" }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun cacheTtlSecondsForPath(path: String): Int? {
        return when (path) {
            "/v1/search" -> 30
            "/v1/cities/search" -> 60
            "/v1/genres/curated" -> 3600
            "/v1/genres/top" -> 3600
            "/v1/artists/popular" -> 3600
            else -> null
        }
    }

    private val offlineCacheRequestInterceptor = Interceptor { chain ->
        val request = chain.request()
        val path = request.url.encodedPath
        val ttlSeconds = cacheTtlSecondsForPath(path)

        if (ttlSeconds == null || request.method != "GET") {
            return@Interceptor chain.proceed(request)
        }

        if (isNetworkAvailable()) {
            return@Interceptor chain.proceed(request)
        }

        val offlineRequest = request.newBuilder()
            .header("Cache-Control", "public, only-if-cached, max-stale=86400")
            .build()
        chain.proceed(offlineRequest)
    }

    private val cacheControlResponseInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        val path = request.url.encodedPath
        val ttlSeconds = cacheTtlSecondsForPath(path)

        if (ttlSeconds == null || request.method != "GET") {
            return@Interceptor response
        }

        response.newBuilder()
            .header("Cache-Control", "public, max-age=$ttlSeconds")
            .build()
    }

    val apiService: ApiService by lazy {
        ensureInitialized()
        retrofit.create(ApiService::class.java)
    }

    fun storeAuth(auth: AuthResponse) {
        ensureInitialized()
        tokenStore.storeAuth(auth)
    }

    fun hasValidAuth(): Boolean {
        ensureInitialized()
        return tokenStore.isTokenValid() && !tokenStore.getAccessToken().isNullOrBlank()
    }

    fun getAccessToken(): String? {
        ensureInitialized()
        return tokenStore.getAccessToken()
    }

    fun clearAuth() {
        ensureInitialized()
        tokenStore.clear()
    }
    
    private val loggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        Log.d("HTTP", "Request: ${request.method} ${request.url}")
        Log.d("HTTP", "Headers: ${request.headers}")
        
        try {
            val response = chain.proceed(request)
            Log.d("HTTP", "Response: ${response.code} ${response.message}")
            Log.d("HTTP", "Response Headers: ${response.headers}")
            response
        } catch (e: Exception) {
            Log.e("HTTP", "Network error: ${e.javaClass.simpleName}: ${e.message}")
            Log.e("HTTP", "Full stack trace:", e)
            throw e
        }
    }
}

private class AuthTokenStore(
    private val prefs: SharedPreferences
) {
    fun getAccessToken(): String? = prefs.getString(NetworkModule.KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(NetworkModule.KEY_REFRESH_TOKEN, null)

    fun isTokenValid(bufferSeconds: Long = 30): Boolean {
        val expiresAt = prefs.getLong(NetworkModule.KEY_EXPIRES_AT_MS, 0L)
        if (expiresAt <= 0L) return false
        return System.currentTimeMillis() + bufferSeconds * 1000L < expiresAt
    }

    @Synchronized
    fun storeAuth(auth: AuthResponse) {
        val expiresAt = System.currentTimeMillis() + auth.expiresIn * 1000L
        prefs.edit()
            .putString(NetworkModule.KEY_ACCESS_TOKEN, auth.token)
            .putString(NetworkModule.KEY_REFRESH_TOKEN, auth.refreshToken)
            .putLong(NetworkModule.KEY_EXPIRES_AT_MS, expiresAt)
            .apply()
    }

    @Synchronized
    fun clear() {
        prefs.edit().clear().apply()
    }
}

private class AuthHeaderInterceptor(
    private val tokenStore: AuthTokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path.startsWith("/v1/auth/")) {
            return chain.proceed(request)
        }

        val token = tokenStore.getAccessToken()
        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val authedRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authedRequest)
    }
}

private class TokenRefreshAuthenticator(
    private val tokenStore: AuthTokenStore,
    private val refreshClient: OkHttpClient
) : Authenticator {
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun authenticate(route: Route?, response: Response): Request? {
        val request = response.request
        val path = request.url.encodedPath

        if (path.startsWith("/v1/auth/")) return null

        if (responseCount(response) >= 2) return null

        val refreshToken = tokenStore.getRefreshToken()
        if (refreshToken.isNullOrBlank()) return null

        synchronized(this) {
            val currentToken = tokenStore.getAccessToken()
            val requestToken = request.header("Authorization")?.removePrefix("Bearer ")?.trim()
            if (!currentToken.isNullOrBlank() && currentToken != requestToken) {
                return request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshRequestBody = gson.toJson(mapOf("token" to refreshToken))
                .toRequestBody(jsonMediaType)
            val refreshRequest = Request.Builder()
                .url("${NetworkModule.BASE_URL}v1/auth/refresh")
                .post(refreshRequestBody)
                .header("Content-Type", "application/json")
                .build()

            val refreshResponse = try {
                refreshClient.newCall(refreshRequest).execute()
            } catch (_: Exception) {
                return null
            }

            refreshResponse.use { rr ->
                if (!rr.isSuccessful) return null
                val bodyString = rr.body?.string() ?: return null
                val auth = try {
                    gson.fromJson(bodyString, AuthResponse::class.java)
                } catch (_: Exception) {
                    return null
                }

                tokenStore.storeAuth(auth)
                return request.newBuilder()
                    .header("Authorization", "Bearer ${auth.token}")
                    .build()
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
