// UserRepo.kt
package com.supermassivecode.stackoverlow.data.local

import android.content.Context
import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiServiceImpl
import com.supermassivecode.stackoverlow.data.remote.User
import com.supermassivecode.stackoverlow.data.remote.UserApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepo(
    private val apiService: StackOverflowApiService,
    private val followStore: FollowStore
) {
    constructor(
        context: Context,
        apiService: StackOverflowApiService = StackOverflowApiServiceImpl()
    ) : this(apiService, SharedPrefsFollowStore(context))

    private var followedUsersCache: MutableSet<Int>? = null

    suspend fun getLatestTopUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val json = apiService.getUsersSortedByReputation(limit = 20)
            val users = UserApiResponse.fromJson(json).items
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isFollowing(userId: Int): Boolean = getFollowedUsers().contains(userId)

    fun toggleFollow(userId: Int): Boolean {
        val set = getFollowedUsers()
        val nowFollowing = if (set.contains(userId)) {
            set.remove(userId); false
        } else {
            set.add(userId); true
        }
        saveFollowedUsers(set)
        return nowFollowing
    }

    private fun getFollowedUsers(): MutableSet<Int> {
        val cached = followedUsersCache
        if (cached != null) return cached
        val loaded = followStore.load()
        followedUsersCache = loaded
        return loaded
    }

    private fun saveFollowedUsers(set: MutableSet<Int>) {
        followedUsersCache = set
        followStore.save(set)
    }
}
