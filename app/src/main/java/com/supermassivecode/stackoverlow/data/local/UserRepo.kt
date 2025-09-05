package com.supermassivecode.stackoverlow.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import com.supermassivecode.stackoverlow.data.remote.User
import com.supermassivecode.stackoverlow.data.remote.UserApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo(
    context: Context,
    private val apiService: StackOverflowApiService = StackOverflowApiService()
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var followedUsersCache: MutableSet<Int>? = null

    suspend fun getLatestTopUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val json = apiService.getUsersSortedByReputation()
            val users = UserApiResponse.fromJson(json).items
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isFollowing(userId: Int): Boolean {
        return getFollowedUsers().contains(userId)
    }

    fun toggleFollow(userId: Int): Boolean {
        val followedUsers = getFollowedUsers()
        val isNowFollowing = if (followedUsers.contains(userId)) {
            followedUsers.remove(userId)
            false
        } else {
            followedUsers.add(userId)
            true
        }
        saveFollowedUsers(followedUsers)
        return isNowFollowing
    }

    private fun getFollowedUsers(): MutableSet<Int> {
        if (followedUsersCache == null) {
            loadFollowedUsers()
        }
        return followedUsersCache!!
    }

    private fun loadFollowedUsers() {
        val followedString = prefs.getString(FOLLOWED_USERS_KEY, "") ?: ""
        followedUsersCache = if (followedString.isNotEmpty()) {
            followedString.split(DELIMITER)
                .mapNotNull { it.toIntOrNull() }
                .toMutableSet()
        } else {
            mutableSetOf()
        }
    }

    private fun saveFollowedUsers(followedUsers: MutableSet<Int>) {
        followedUsersCache = followedUsers
        prefs.edit {
            putString(FOLLOWED_USERS_KEY, followedUsers.joinToString(DELIMITER))
        }
    }

    companion object {
        private const val PREFS_NAME = "user_follows"
        private const val FOLLOWED_USERS_KEY = "followed_users"
        private const val DELIMITER = ","
    }
}