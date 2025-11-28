package com.example.gamehaven.other

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

    object CurrentUserManager {
        private var sessionManager: SessionManager? = null

        fun initialize(context: Context) {
            sessionManager = SessionManager(context)
        }

        fun getUserId(): Int {
            return sessionManager?.getUserId() ?: -1
        }

        fun getUsername(): String {
            return sessionManager?.getUsername() ?: ""
        }

        fun getEmail(): String {
            return sessionManager?.getEmail() ?: ""
        }

        fun getRole(): Boolean {
            return sessionManager?.getRole() ?: false
        }

        fun isLoggedIn(): Boolean {
            return sessionManager?.isLoggedIn() ?: false
        }

        fun logout() {
            sessionManager?.clearSession()
        }
    }

@Composable
fun rememberCurrentUser(): CurrentUserManager {
    val context = LocalContext.current
    return remember {
        CurrentUserManager.also {
            it.initialize(context)
        }
    }
}
