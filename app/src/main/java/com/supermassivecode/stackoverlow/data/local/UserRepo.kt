package com.supermassivecode.stackoverlow.data.local

import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import com.supermassivecode.stackoverlow.data.remote.User
import com.supermassivecode.stackoverlow.data.remote.UserApiResponse

class UserRepo(
    private val apiService: StackOverflowApiService = StackOverflowApiService()
) {
    fun getTopUsers(): List<User> {
        val json = apiService.getUsersSortedByReputation()
        return UserApiResponse.fromJson(json).items
    }
}