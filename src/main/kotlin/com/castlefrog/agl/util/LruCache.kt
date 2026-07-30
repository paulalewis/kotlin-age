package com.castlefrog.agl.util

/**
 * Implementation of least recently used cache.
 *
 * Evicts the eldest entry when the map grows beyond [cacheSize], so a cache of size 1
 * retains a single entry (using `size > cacheSize`, not `size >= cacheSize`).
 */
class LruCache<K, V>(private val cacheSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean = size > cacheSize
}