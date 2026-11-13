package com.injuv.voluntariado.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ForumPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun messages(programId: Int): List<ForumMessage> = load(key(programId))
    fun messagesOrg(orgId: Int): List<ForumMessage> = load(orgKey(orgId))

    fun append(programId: Int, message: ForumMessage) = save(key(programId), load(key(programId)) + message)
    fun appendOrg(orgId: Int, message: ForumMessage) = save(orgKey(orgId), load(orgKey(orgId)) + message)

    fun seedIfEmpty(programId: Int, message: ForumMessage): List<ForumMessage> {
        if (messages(programId).isEmpty()) append(programId, message)
        return messages(programId)
    }

    fun seedOrgIfEmpty(orgId: Int, message: ForumMessage): List<ForumMessage> {
        if (messagesOrg(orgId).isEmpty()) appendOrg(orgId, message)
        return messagesOrg(orgId)
    }

    private fun load(k: String): List<ForumMessage> {
        val raw = prefs.getString(k, null) ?: return emptyList()
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

    private fun save(k: String, list: List<ForumMessage>) {
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
        prefs.edit().putString(k, array.toString()).apply()
    }

    private fun key(programId: Int) = "$KEY_PREFIX$programId"
    private fun orgKey(orgId: Int) = "$ORG_PREFIX$orgId"

    companion object {
        private const val PREFS_NAME = "injuv_forum"
        private const val KEY_PREFIX = "forum_"
        private const val ORG_PREFIX = "orgchat_"
    }
}
