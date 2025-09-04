package com.supermassivecode.stackoverlow.data.remote
import org.json.JSONObject

data class User(
    val reputation: Int,
    val userId: Int,
    val displayName: String,
    val profileImage: String?
)

data class UserApiResponse(
    val items: List<User>
) {
    companion object {
        fun fromJson(json: String): UserApiResponse {
            val root = JSONObject(json)
            val itemsJson = root.getJSONArray("items")
            val users = buildList {
                for (i in 0 until itemsJson.length()) {
                    val o = itemsJson.getJSONObject(i)
                    add(
                        User(
                            reputation = o.optInt("reputation"),
                            userId = o.optInt("user_id"),
                            displayName = o.optString("display_name"),
                            profileImage = o.optString("profile_image")
                        )
                    )
                }
            }
            return UserApiResponse(items = users)
        }
    }
}
