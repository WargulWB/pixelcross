package com.github.wargulwb.pixelcross.utils;

import org.apache.commons.lang3.Strings;

public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean fromString(final String value) {
        if (value == null) {
            return null;
        }
        return Strings.CI.equals("true", value);
    }

    @Override
    public String toString(final Boolean value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
