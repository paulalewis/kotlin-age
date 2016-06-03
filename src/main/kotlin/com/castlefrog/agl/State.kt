package com.castlefrog.agl

interface State<S : State<S>> : Copyable<S>
