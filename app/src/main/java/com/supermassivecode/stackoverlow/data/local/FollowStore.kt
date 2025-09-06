// FollowStore.kt
package com.supermassivecode.stackoverlow.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

interface FollowStore {
    fun load(): MutableSet<Int>
    fun save(ids: MutableSet<Int>)
}

class SharedPrefsFollowStore(
    context: Context,
) : FollowStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun load(): MutableSet<Int> {
        val s = prefs.getString(FOLLOWED_USERS_KEY, "") ?: ""
        return if (s.isBlank()) mutableSetOf()
        else s.split(DELIMITER)
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()
    }

    override fun save(ids: MutableSet<Int>) {
        prefs.edit { putString(FOLLOWED_USERS_KEY, ids.joinToString(DELIMITER)) }
    }

    companion object {
        private const val PREFS_NAME = "user_follows"
        private const val FOLLOWED_USERS_KEY = "followed_users"
        private const val DELIMITER = ","
    }
}
