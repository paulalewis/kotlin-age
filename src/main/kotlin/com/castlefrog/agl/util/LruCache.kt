package com.castlefrog.agl.util

/**
 * Implementation of least recently used cache.
 */
class LruCache<K, V>(private val cacheSize: Int) : LinkedHashMap<K, V>(16, 0.75f, true) {
    override fun removeEldestEntry(eldest: Map.Entry<K, V>): Boolean = size >= cacheSize
}