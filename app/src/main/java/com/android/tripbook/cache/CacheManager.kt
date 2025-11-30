package com.tripscheduler.cache

import com.tripscheduler.data.AppJson
import com.tripscheduler.data.Location
import com.tripscheduler.data.Trip
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

object CacheManager {

    private lateinit var jedisPool: JedisPool
    private const val CACHE_TTL_SECONDS = 300 // 5 minutes TTL for cached items

    fun init() {
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = 128
        poolConfig.maxIdle = 128
        poolConfig.minIdle = 16
        poolConfig.testOnBorrow = true // Test connection on borrow
        poolConfig.testOnReturn = true // Test connection on return
        poolConfig.testWhileIdle = true // Test connection while idle
        poolConfig.minEvictableIdleTimeMillis = 60000 // 1 minute
        poolConfig.timeBetweenEvictionRunsMillis = 30000 // 30 seconds
        poolConfig.numTestsPerEvictionRun = 3

        jedisPool = JedisPool(poolConfig, "localhost", 6379)
        println("Redis cache pool initialized.")
    }

    fun close() {
        jedisPool.close()
        println("Redis cache pool closed.")
    }

    private inline fun <reified T> getFromCache(key: String): T? {
        jedisPool.resource.use { jedis ->
            val json = jedis.get(key)
            return if (json != null) {
                println("Cache HIT for key: $key")
                AppJson.decodeFromString<T>(json)
            } else {
                println("Cache MISS for key: $key")
                null
            }
        }
    }

    private inline fun <reified T> putInCache(key: String, value: T) {
        jedisPool.resource.use { jedis ->
            val json = AppJson.encodeToString(value)
            jedis.setex(key, CACHE_TTL_SECONDS, json)
            println("Stored in cache: $key (TTL: $CACHE_TTL_SECONDS seconds)")
        }
    }

    // --- Trip Cache Operations ---
    fun getTrip(tripId: String): Trip? {
        return getFromCache("trip:$tripId")
    }

    fun cacheTrip(trip: Trip) {
        putInCache("trip:${trip.id}", trip)
    }

    // --- Location Cache Operations ---
    fun getLocation(locationId: String): Location? {
        return getFromCache("location:$locationId")
    }

    fun cacheLocation(location: Location) {
        putInCache("location:${location.id}", location)
    }

    // You can add more cache operations for ItineraryItem if needed,
    // but for this example, focusing on Trip and Location is sufficient.
}