package com.supermassivecode.stackoverlow.data.local

import android.content.Context
import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import com.supermassivecode.stackoverlow.data.remote.User
import com.supermassivecode.stackoverlow.data.remote.UserApiResponse
import androidx.core.content.edit

class UserRepo(
    context: Context,
    private val apiService: StackOverflowApiService = StackOverflowApiService()
) {
    private val prefs = context.getSharedPreferences("user_follows", Context.MODE_PRIVATE)

    fun getLatestTopUsers(): List<User> {
        val json = apiService.getUsersSortedByReputation()
        return UserApiResponse.fromJson(json).items
    }

    fun isFollowing(userId: Int): Boolean {
        return loadFollowedUsers().contains(userId)
    }

    fun toggleFollow(userId: Int): Boolean {
        val followedUsers = loadFollowedUsers()
        val isFollowing = if (followedUsers.contains(userId)) {
            followedUsers.remove(userId)
            false
        } else {
            followedUsers.add(userId)
            true
        }
        saveFollowedUsers(followedUsers)
        return isFollowing
    }

    private fun loadFollowedUsers(): MutableSet<Int> {
        val followed = mutableSetOf<Int>()
        val followedString = prefs.getString("followed_users", "") ?: ""
        if (followedString.isNotEmpty()) {
            followed.addAll(
                followedString.split(",").mapNotNull { it.toIntOrNull() }
            )
        }
        return followed
    }
    private fun saveFollowedUsers(followedUsers: MutableSet<Int>) {
        prefs.edit {
            putString("followed_users", followedUsers.joinToString(","))
        }
    }
}