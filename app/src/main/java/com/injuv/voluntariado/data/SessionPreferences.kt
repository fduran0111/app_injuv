package com.injuv.voluntariado.data

import android.content.Context

class SessionPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val accounts = context.applicationContext.getSharedPreferences(ACCOUNTS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun displayName(): String = prefs.getString(KEY_NAME, "Voluntario") ?: "Voluntario"

    fun email(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun login(name: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_NAME)
            .remove(KEY_EMAIL)
            .apply()
    }

    /** Nombre con el que se registró una cuenta, indexado por email. Sobrevive al logout. */
    fun registeredName(email: String): String? = accounts.getString(email.trim().lowercase(), null)

    fun registerAccount(name: String, email: String) {
        accounts.edit().putString(email.trim().lowercase(), name).apply()
    }

    fun isTutor(): Boolean = prefs.getBoolean(KEY_TUTOR, false)

    fun setTutor(isTutor: Boolean) {
        prefs.edit().putBoolean(KEY_TUTOR, isTutor).apply()
    }

    fun tutorProgramId(): Int = prefs.getInt(KEY_TUTOR_PROGRAM, 0)

    fun setTutorProgramId(id: Int) {
        prefs.edit().putInt(KEY_TUTOR_PROGRAM, id).apply()
    }

    fun isOrg(): Boolean = prefs.getBoolean(KEY_ORG, false)

    fun setOrg(isOrg: Boolean) {
        prefs.edit().putBoolean(KEY_ORG, isOrg).apply()
    }

    companion object {
        private const val PREFS_NAME = "injuv_prefs"
        private const val ACCOUNTS_NAME = "injuv_accounts"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_TUTOR = "is_tutor"
        private const val KEY_TUTOR_PROGRAM = "tutor_program_id"
        private const val KEY_ORG = "is_org"
    }
}
