package com.castlefrog.agl

interface Action<A : Action<A>> : Copyable<A>
