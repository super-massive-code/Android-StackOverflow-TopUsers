# Stackoverlow — Top 20 Users

A minimal Android app that lists Stack Overflow’s top 20 users and lets you “follow” them locally (no server state).

## If this were a production app

- **Networking:** Use **Retrofit** with **OkHttp** (interceptors, timeouts, logging).
- **Images:** Use **Coil** for loading, transformations, and lifecycle-aware caching.
- **Caching:** Cache Stack Overflow responses (HTTP/JSON cache and/or local DB/DataStore) with sensible TTLs and offline support.
- **Testing:** Use **OkHttp MockWebServer** to simulate API responses and test system boundaries and correctness without mocking intermediaries.
- **Additional features:** Pull-to-refresh, optional background **WorkManager** job to refresh, and tap-through to each user’s Stack Overflow profile.
