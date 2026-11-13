package com.injuv.voluntariado.data

import android.content.Context

class OrgPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): OrgProfile = OrgProfile(
        name = prefs.getString(KEY_NAME, "") ?: "",
        about = prefs.getString(KEY_ABOUT, "") ?: "",
        email = prefs.getString(KEY_EMAIL, "") ?: "",
        phone = prefs.getString(KEY_PHONE, "") ?: "",
        website = prefs.getString(KEY_WEBSITE, "") ?: "",
    )

    fun save(org: OrgProfile) {
        prefs.edit()
            .putString(KEY_NAME, org.name)
            .putString(KEY_ABOUT, org.about)
            .putString(KEY_EMAIL, org.email)
            .putString(KEY_PHONE, org.phone)
            .putString(KEY_WEBSITE, org.website)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "injuv_org"
        private const val KEY_NAME = "org_name"
        private const val KEY_ABOUT = "org_about"
        private const val KEY_EMAIL = "org_email"
        private const val KEY_PHONE = "org_phone"
        private const val KEY_WEBSITE = "org_website"
    }
}
