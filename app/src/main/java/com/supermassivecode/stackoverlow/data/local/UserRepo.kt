package com.supermassivecode.stackoverlow.data.local

import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import com.supermassivecode.stackoverlow.data.remote.UserApiResponse

class UserRepo(
    private val apiService: StackOverflowApiService = StackOverflowApiService()
) {
    fun getTopUsers() {
        val json = apiService.getUsersSortedByReputation()
        val parsed = UserApiResponse.fromJson(json)
        print("")
    }
}