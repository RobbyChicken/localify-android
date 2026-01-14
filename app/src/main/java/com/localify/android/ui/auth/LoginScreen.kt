package com.localify.android.ui.auth

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.localify.android.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Path
import kotlin.random.Random

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    spotifyCallbackUri: Uri? = null,
    onConsumeSpotifyCallback: () -> Unit = {},
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val googleWebClientId = remember {
        context.getString(R.string.google_web_client_id).trim()
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val token = account.idToken
            if (token.isNullOrBlank()) {
                viewModel.handleGoogleIdToken("", onSuccess = onLoginSuccess)
            } else {
                viewModel.handleGoogleIdToken(token, onSuccess = onLoginSuccess)
            }
        } catch (e: Exception) {
            // surfacing via viewmodel keeps UI consistent
            viewModel.handleGoogleIdToken("", onSuccess = onLoginSuccess)
        }
    }

    var showEmailDialog by remember { mutableStateOf(false) }

    LaunchedEffect(spotifyCallbackUri) {
        if (spotifyCallbackUri != null) {
            viewModel.handleSpotifyRedirect(spotifyCallbackUri, onSuccess = onLoginSuccess)
            onConsumeSpotifyCallback()
        }
    }

    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = { Text("Log in with Email") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = { Text("Email") },
                        singleLine = true,
                        enabled = !uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.emailNonce != null) {
                        OutlinedTextField(
                            value = uiState.emailCode,
                            onValueChange = { viewModel.updateEmailCode(it) },
                            label = { Text("Code") },
                            singleLine = true,
                            enabled = !uiState.isLoading
                        )
                    }

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                if (uiState.emailNonce == null) {
                    TextButton(
                        onClick = { viewModel.sendEmailLoginCode() },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Send code")
                    }
                } else {
                    TextButton(
                        onClick = { viewModel.verifyEmailLogin(onSuccess = onLoginSuccess) },
                        enabled = !uiState.isLoading
                    ) {
                        Text("Verify")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEmailDialog = false },
                    enabled = !uiState.isLoading
                ) {
                    Text("Close")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map-style background
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw a dark map-like background
            drawRect(
                color = Color(0xFF1a1a1a),
                size = size
            )
            
            // Draw map-like grid lines
            val gridSpacing = 100f
            for (i in 0..size.width.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF333333),
                    start = androidx.compose.ui.geometry.Offset(i.toFloat(), 0f),
                    end = androidx.compose.ui.geometry.Offset(i.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (i in 0..size.height.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFF333333),
                    start = androidx.compose.ui.geometry.Offset(0f, i.toFloat()),
                    end = androidx.compose.ui.geometry.Offset(size.width, i.toFloat()),
                    strokeWidth = 1f
                )
            }
            
            // Draw some map-like shapes (roads/areas)
            val path = Path()
            path.moveTo(size.width * 0.2f, size.height * 0.3f)
            path.quadraticBezierTo(
                size.width * 0.5f, size.height * 0.1f,
                size.width * 0.8f, size.height * 0.4f
            )
            path.lineTo(size.width * 0.9f, size.height * 0.6f)
            path.quadraticBezierTo(
                size.width * 0.7f, size.height * 0.8f,
                size.width * 0.3f, size.height * 0.7f
            )
            path.close()
            
            drawPath(
                path = path,
                color = Color(0xFF2a2a2a)
            )
        }
        
        // Background Image overlay (if you still want the original image)
        Image(
            painter = painterResource(id = R.drawable.background_localify),
            contentDescription = "Localify background",
            modifier = Modifier.fillMaxSize().alpha(0.3f),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )
        
        // Localify banner
        Image(
            painter = painterResource(id = R.drawable.localify_banner),
            contentDescription = "Localify Banner",
            modifier = Modifier
                .padding(start = 16.dp, top = 80.dp)
                .fillMaxWidth(0.8f),
            contentScale = ContentScale.Fit
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.weight(1f))
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Login Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Login
                Button(
                    onClick = {
                        if (googleWebClientId.isBlank()) {
                            viewModel.setError("Missing GOOGLE_WEB_CLIENT_ID configuration")
                            return@Button
                        }

                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestIdToken(googleWebClientId)
                            .build()
                        val client = GoogleSignIn.getClient(context, gso)
                        googleLauncher.launch(client.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.Black
                        )
                    }
                }

                // Spotify Login
                Button(
                    onClick = { viewModel.startSpotifyLogin(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "♫",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Login with Spotify",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
                
                // Email Login
                Button(
                    onClick = { showEmailDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📧",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log in with Email",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
                
                // Guest Mode
                Button(
                    onClick = { viewModel.continueAsGuest(onSuccess = onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue as Guest",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (uiState.error != null && !showEmailDialog) {
                Text(
                    text = uiState.error ?: "",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
