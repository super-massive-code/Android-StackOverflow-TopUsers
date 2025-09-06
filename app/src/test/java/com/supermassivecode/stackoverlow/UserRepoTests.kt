package com.supermassivecode.stackoverlow

import com.supermassivecode.stackoverlow.data.local.FollowStore
import com.supermassivecode.stackoverlow.data.local.UserRepo
import com.supermassivecode.stackoverlow.data.remote.StackOverflowApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class UserRepoTest {
    private class FakeFollowStore(initial: Set<Int> = emptySet()) : FollowStore {
        var loadCount = 0
        var saveCount = 0
        private var data: MutableSet<Int> = initial.toMutableSet()

        override fun load(): MutableSet<Int> {
            loadCount++
            return data.toMutableSet()
        }

        override fun save(ids: MutableSet<Int>) {
            saveCount++
            data = ids.toMutableSet()
        }
    }

    private class FakeApiService(
        private val json: String? = null,
        private val toThrow: Throwable? = null
    ) : StackOverflowApiService {
        override fun getUsersSortedByReputation(limit: Int): String {
            toThrow?.let { throw it }
            return json ?: error("No JSON configured")
        }
    }

    private val sampleJson = """
        {
          "items": [
            { "user_id": 101, "display_name": "Terry", "profile_image": "https://blah1.jpg" },
            { "user_id": 202, "display_name": "June",   "profile_image": "https://blah2.jpg2" }
          ]        
        }
    """.trimIndent()

    @Test
    fun given_validApiResponse_when_getLatestTopUsers_called_then_returnsParsedList() = runBlocking {
        val api = FakeApiService(json = sampleJson)
        val store = FakeFollowStore()
        val repo = UserRepo(api, store)

        val result = repo.getLatestTopUsers()
        assertTrue(result.isSuccess)

        val users = result.getOrThrow()
        assertEquals(2, users.size)

        val ids = users.map { it.userId }
        assertEquals(listOf(101, 202), ids)
    }

    @Test
    fun given_apiThrows_when_getLatestTopUsers_called_then_returnsFailure() = runBlocking {
        val api = FakeApiService(toThrow = IOException("oops"))
        val repo = UserRepo(api, FakeFollowStore())

        val result = repo.getLatestTopUsers()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun given_userToggleFollow_called_then_becomesFollowed_then_becomesUnfollowed() {
        val store = FakeFollowStore()
        val repo = UserRepo(FakeApiService(json = sampleJson), store)

        // Initially not following
        assertFalse(repo.isFollowing(42))

        // Toggle follow -> returns new state true
        val nowFollowing = repo.toggleFollow(42)
        assertTrue(nowFollowing)
        assertTrue(repo.isFollowing(42))

        // Toggle again -> returns new state false
        val nowNotFollowing = repo.toggleFollow(42)
        assertFalse(nowNotFollowing)
        assertFalse(repo.isFollowing(42))
    }
}
