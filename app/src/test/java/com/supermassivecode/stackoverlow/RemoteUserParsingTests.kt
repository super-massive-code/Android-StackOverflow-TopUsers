package com.supermassivecode.stackoverlow

import com.supermassivecode.stackoverlow.data.remote.UserApiResponse
import org.junit.Test
import org.junit.Assert.*
import org.json.JSONException

class RemoteUserParsingTests {

    @Test
    fun givenValidJsonWithSingleUser_whenParsed_thenReturnsThatUser() {
        val json = """
            {
              "items": [
                {
                  "reputation": 500000,
                  "user_id": 22656,
                  "display_name": "Bob Jones",
                  "profile_image": "https://blah.com/image.png"
                }
              ]
            }
        """.trimIndent()

        val resp = UserApiResponse.fromJson(json)
        assertEquals(1, resp.items.size)

        val user = resp.items.first()
        assertEquals(500000, user.reputation)
        assertEquals(22656, user.userId)
        assertEquals("Bob Jones", user.displayName)
        assertEquals("https://blah.com/image.png", user.profileImage)
    }

    @Test
    fun givenValidJsonWithMultipleUsers_whenParsed_thenPreservesOrder() {
        val json = """
            {
              "items": [
                { "reputation": 10, "user_id": 1, "display_name": "A", "profile_image": "imgA" },
                { "reputation": 20, "user_id": 2, "display_name": "B", "profile_image": "imgB" },
                { "reputation": 30, "user_id": 3, "display_name": "C", "profile_image": "imgC" }
              ]
            }
        """.trimIndent()

        val resp = UserApiResponse.fromJson(json)
        assertEquals(listOf(10, 20, 30), resp.items.map { it.reputation })
        assertEquals(listOf(1, 2, 3), resp.items.map { it.userId })
        assertEquals(listOf("A", "B", "C"), resp.items.map { it.displayName })
    }

    @Test
    fun givenJsonWithEmptyItemsArray_whenParsed_thenReturnsEmptyList() {
        val json = """{ "items": [] }"""
        val resp = UserApiResponse.fromJson(json)
        assertTrue(resp.items.isEmpty())
    }

    @Test(expected = JSONException::class)
    fun givenJsonWithoutItemsKey_whenParsed_thenThrowsJSONException() {
        val json = """{ "a_load_of_bobbins": [] }"""
        UserApiResponse.fromJson(json)
    }

    @Test(expected = JSONException::class)
    fun givenInvalidJson_whenParsed_thenThrowsJSONException() {
        val json = """{ "items": [ """
        UserApiResponse.fromJson(json)
    }
}
