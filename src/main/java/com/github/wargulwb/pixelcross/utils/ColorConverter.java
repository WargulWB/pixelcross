package com.github.wargulwb.pixelcross.utils;

import java.awt.Color;
import java.util.Objects;

import jakarta.inject.Inject;

public class ColorConverter implements Converter<Color> {

    private final ColorUtils colorUtils;

    @Inject
    ColorConverter(final ColorUtils colorUtils) {
        this.colorUtils = Objects.requireNonNull(colorUtils, getClass().getSimpleName() + ".colorUtils cannot be null!");
    }

    @Override
    public Color fromString(final String value) {
        if (value == null) {
            return null;
        }
        return colorUtils.colorFromRGBHexString(value);
    }

    @Override
    public String toString(final Color value) {
        if (value == null) {
            return null;
        }
        return colorUtils.colorToRGBHexString(value);
    }

}
