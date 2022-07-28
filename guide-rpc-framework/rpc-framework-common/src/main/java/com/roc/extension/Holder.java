package com.roc.extension;

/**
 * @Description Holder
 * @Author penn
 * @Date 2022/7/28 22:57
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
