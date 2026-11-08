package com.injuv.voluntariado.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ProfilePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(defaultName: String, defaultEmail: String): UserProfile {
        val privacy = PrivacySettings(
            volunteerHours = prefs.getBoolean(KEY_PRIV_HOURS, true),
            age = prefs.getBoolean(KEY_PRIV_AGE, true),
            email = prefs.getBoolean(KEY_PRIV_EMAIL, true),
            phone = prefs.getBoolean(KEY_PRIV_PHONE, true),
            location = prefs.getBoolean(KEY_PRIV_LOCATION, true),
            aboutMe = prefs.getBoolean(KEY_PRIV_ABOUT, true),
            volunteerExperience = prefs.getBoolean(KEY_PRIV_EXPERIENCE, true),
            skills = prefs.getBoolean(KEY_PRIV_SKILLS, true),
            reviews = prefs.getBoolean(KEY_PRIV_REVIEWS, true),
        )
        return UserProfile(
            name = prefs.getString(KEY_NAME, defaultName) ?: defaultName,
            email = prefs.getString(KEY_EMAIL, defaultEmail) ?: defaultEmail,
            phone = prefs.getString(KEY_PHONE, "") ?: "",
            age = prefs.getString(KEY_AGE, "") ?: "",
            region = prefs.getString(KEY_REGION, "") ?: "",
            city = prefs.getString(KEY_CITY, "") ?: "",
            commune = prefs.getString(KEY_COMMUNE, "") ?: "",
            about = prefs.getString(KEY_ABOUT, "") ?: "",
            skills = decodeSkills(prefs.getString(KEY_SKILLS, null)),
            bannerIndex = prefs.getInt(KEY_BANNER, 0),
            photoUri = prefs.getString(KEY_PHOTO, null),
            privacy = privacy,
        )
    }

    fun save(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_NAME, profile.name)
            .putString(KEY_EMAIL, profile.email)
            .putString(KEY_PHONE, profile.phone)
            .putString(KEY_AGE, profile.age)
            .putString(KEY_REGION, profile.region)
            .putString(KEY_CITY, profile.city)
            .putString(KEY_COMMUNE, profile.commune)
            .putString(KEY_ABOUT, profile.about)
            .putString(KEY_SKILLS, encodeSkills(profile.skills))
            .putInt(KEY_BANNER, profile.bannerIndex)
            .putString(KEY_PHOTO, profile.photoUri)
            .putBoolean(KEY_PRIV_HOURS, profile.privacy.volunteerHours)
            .putBoolean(KEY_PRIV_AGE, profile.privacy.age)
            .putBoolean(KEY_PRIV_EMAIL, profile.privacy.email)
            .putBoolean(KEY_PRIV_PHONE, profile.privacy.phone)
            .putBoolean(KEY_PRIV_LOCATION, profile.privacy.location)
            .putBoolean(KEY_PRIV_ABOUT, profile.privacy.aboutMe)
            .putBoolean(KEY_PRIV_EXPERIENCE, profile.privacy.volunteerExperience)
            .putBoolean(KEY_PRIV_SKILLS, profile.privacy.skills)
            .putBoolean(KEY_PRIV_REVIEWS, profile.privacy.reviews)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun encodeSkills(skills: List<Skill>): String {
        val array = JSONArray()
        skills.forEach { skill ->
            array.put(JSONObject().put("name", skill.name).put("level", skill.level))
        }
        return array.toString()
    }

    private fun decodeSkills(raw: String?): List<Skill> {
        if (raw.isNullOrBlank()) return defaultSkills
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Skill(obj.getString("name"), obj.getInt("level"))
            }
        }.getOrDefault(defaultSkills)
    }

    companion object {
        private const val PREFS_NAME = "injuv_profile"
        private const val KEY_NAME = "profile_name"
        private const val KEY_EMAIL = "profile_email"
        private const val KEY_PHONE = "profile_phone"
        private const val KEY_AGE = "profile_age"
        private const val KEY_REGION = "profile_region"
        private const val KEY_CITY = "profile_city"
        private const val KEY_COMMUNE = "profile_commune"
        private const val KEY_ABOUT = "profile_about"
        private const val KEY_SKILLS = "profile_skills"
        private const val KEY_BANNER = "profile_banner"
        private const val KEY_PHOTO = "profile_photo"
        private const val KEY_PRIV_HOURS = "priv_hours"
        private const val KEY_PRIV_AGE = "priv_age"
        private const val KEY_PRIV_EMAIL = "priv_email"
        private const val KEY_PRIV_PHONE = "priv_phone"
        private const val KEY_PRIV_LOCATION = "priv_location"
        private const val KEY_PRIV_ABOUT = "priv_about"
        private const val KEY_PRIV_EXPERIENCE = "priv_experience"
        private const val KEY_PRIV_SKILLS = "priv_skills"
        private const val KEY_PRIV_REVIEWS = "priv_reviews"

        val defaultSkills = listOf(
            Skill("Trabajo en Equipo", 50),
            Skill("Comunicación Efectiva", 50),
            Skill("Liderazgo", 50),
            Skill("Empatía", 50),
        )
    }
}
