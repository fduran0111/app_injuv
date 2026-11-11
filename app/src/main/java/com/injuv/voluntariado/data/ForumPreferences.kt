package com.injuv.voluntariado.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ForumPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun messages(programId: Int): List<ForumMessage> {
        val raw = prefs.getString(key(programId), null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val item = array.getJSONObject(i)
                ForumMessage(
                    id = item.getInt("id"),
                    author = item.getString("author"),
                    text = item.getString("text"),
                    date = item.getString("date"),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun append(programId: Int, message: ForumMessage) {
        val list = messages(programId) + message
        val array = JSONArray()
        list.forEach { msg ->
            array.put(
                JSONObject()
                    .put("id", msg.id)
                    .put("author", msg.author)
                    .put("text", msg.text)
                    .put("date", msg.date),
            )
        }
        prefs.edit().putString(key(programId), array.toString()).apply()
    }

    fun seedIfEmpty(programId: Int, message: ForumMessage): List<ForumMessage> {
        val existing = messages(programId)
        if (existing.isEmpty()) append(programId, message)
        return messages(programId)
    }

    private fun key(programId: Int) = "$KEY_PREFIX$programId"

    companion object {
        private const val PREFS_NAME = "injuv_forum"
        private const val KEY_PREFIX = "forum_"
    }
}
