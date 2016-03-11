package com.castlefrog.agl

interface Copyable<out T> {
    fun copy(): T
}
