package com.example.gamehaven.other

    import android.content.Context
    import android.content.SharedPreferences

    class SessionManager(context: Context) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

        companion object {
            private const val KEY_USER_ID = "user_id"
            private const val KEY_USERNAME = "username"
            private const val KEY_EMAIL = "email"
            private const val KEY_ROLE = "role"
            private const val KEY_IS_LOGGED_IN = "is_logged_in"
        }

        fun saveUserSession(userId: Int, username: String, email: String, role: Boolean) {
            with(sharedPreferences.edit()) {
                putInt(KEY_USER_ID, userId)
                putString(KEY_USERNAME, username)
                putString(KEY_EMAIL, email)
                putBoolean(KEY_ROLE, role)
                putBoolean(KEY_IS_LOGGED_IN, true)
                apply()
            }
        }

        fun getUserId(): Int {
            return sharedPreferences.getInt(KEY_USER_ID, -1)
        }

        fun getUsername(): String {
            return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        }

        fun getEmail(): String {
            return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
        }

        fun getRole(): Boolean {
            return sharedPreferences.getBoolean(KEY_ROLE, false)
        }

        fun isLoggedIn(): Boolean {
            return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        }

        fun clearSession() {
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
        }
    }
