package com.castlefrog.agl.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LruCacheTest {

    @Test
    fun sizeOneRetainsSingleEntry() {
        val cache = LruCache<String, Int>(1)
        cache["a"] = 1
        assertEquals(1, cache.size)
        assertEquals(1, cache["a"])
    }

    @Test
    fun sizeOneEvictsPreviousEntryOnInsert() {
        val cache = LruCache<String, Int>(1)
        cache["a"] = 1
        cache["b"] = 2
        assertEquals(1, cache.size)
        assertNull(cache["a"])
        assertEquals(2, cache["b"])
    }

    @Test
    fun sizeOneUpdateSameKeyDoesNotEvict() {
        val cache = LruCache<String, Int>(1)
        cache["a"] = 1
        cache["a"] = 2
        assertEquals(1, cache.size)
        assertEquals(2, cache["a"])
    }

    @Test
    fun largerCacheRetainsUpToCapacity() {
        val cache = LruCache<String, Int>(3)
        cache["a"] = 1
        cache["b"] = 2
        cache["c"] = 3
        assertEquals(3, cache.size)
        assertEquals(1, cache["a"])
        assertEquals(2, cache["b"])
        assertEquals(3, cache["c"])
    }

    @Test
    fun largerCacheEvictsEldestWhenOverCapacity() {
        val cache = LruCache<String, Int>(2)
        cache["a"] = 1
        cache["b"] = 2
        cache["c"] = 3
        assertEquals(2, cache.size)
        assertNull(cache["a"])
        assertEquals(2, cache["b"])
        assertEquals(3, cache["c"])
    }

    @Test
    fun accessOrderPromotesEntrySoItIsNotEldest() {
        val cache = LruCache<String, Int>(2)
        cache["a"] = 1
        cache["b"] = 2
        // Access "a" so it becomes most-recently used; "b" is eldest.
        assertEquals(1, cache["a"])
        cache["c"] = 3
        assertEquals(2, cache.size)
        assertEquals(1, cache["a"])
        assertNull(cache["b"])
        assertEquals(3, cache["c"])
    }

    @Test
    fun containsKeyReflectsPresence() {
        val cache = LruCache<Int, String>(2)
        cache[1] = "one"
        assertTrue(cache.containsKey(1))
        assertFalse(cache.containsKey(2))
    }
}
