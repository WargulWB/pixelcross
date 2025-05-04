package com.github.wargulwb.pixelcross.utils;

public interface Converter<T> {

    T fromString(final String value);

    String toString(final T value);

}
