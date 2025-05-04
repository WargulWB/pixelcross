package com.github.wargulwb.pixelcross.utils;

import org.apache.commons.lang3.StringUtils;

public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean fromString(final String value) {
        if (value == null) {
            return null;
        }
        return StringUtils.equalsIgnoreCase("true", value);
    }

    @Override
    public String toString(final Boolean value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
