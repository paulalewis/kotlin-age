package com.castlefrog.agl

interface State<out T : State<T>> {
    fun copy(): T
}
