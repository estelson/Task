package com.exemplo.task.helper

import com.exemplo.task.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    companion object {
        fun getDatabase() = FirebaseDatabase.getInstance().reference

        private fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().uid

        fun isAuthenticated() = getAuth().currentUser != null

        fun validateErrors(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier") -> {
                    R.string.fb_auth_error_account_not_registered
                }

                error.contains("The email address is badly formatted") -> {
                    R.string.fb_auth_error_invalid_email
                }

                error.contains("The password is invalid or the user does not have a password") -> {
                    R.string.fb_auth_error_invalid_password
                }

                error.contains("The email address is already in use by another account") -> {
                    R.string.fb_auth_error_email_already_in_use
                }

                error.contains("The given password is invalid. [ Password should be at least 6 characters ]") -> {
                    R.string.fb_auth_error_strong_password
                }

                else -> {
                    R.string.generic_error
                }
            }
        }
    }

}