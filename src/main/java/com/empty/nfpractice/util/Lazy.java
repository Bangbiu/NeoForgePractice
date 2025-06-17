package com.empty.nfpractice.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;
    private boolean initialized = false;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!initialized) {
            value = supplier.get();
            initialized = true;
        }
        return value;
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }
}