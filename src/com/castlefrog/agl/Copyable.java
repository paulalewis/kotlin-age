package com.castlefrog.agl;

public interface Copyable<T extends Copyable<T>> {
    T copy();
}
