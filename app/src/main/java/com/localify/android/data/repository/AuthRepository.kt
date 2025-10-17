package com.localify.android.data.repository

class AuthRepository {
    
    fun getAuthHeader(): String? {
        // Return null to simulate unauthenticated state for testing
        return null
    }
    
    fun isAuthenticated(): Boolean {
        return getAuthHeader() != null
    }
}
