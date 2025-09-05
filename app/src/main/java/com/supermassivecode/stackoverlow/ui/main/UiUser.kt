package com.supermassivecode.stackoverlow.ui.main

data class UiUser(
    val name: String,
    val imageUrl: String?,
    val reputationScore: Int,
    val followed: Boolean = false
)

