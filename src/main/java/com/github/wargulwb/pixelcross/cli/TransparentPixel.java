package com.github.wargulwb.pixelcross.cli;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Strings;

import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;

public class TransparentPixel {

    private static final String TOP_LEFT = "top_left";
    private static final String TOP_RIGHT = "top_right";
    private static final String BOTTOM_LEFT = "bottom_left";
    private static final String BOTTOM_RIGHT = "bottom_right";

    private static final String[] SUPPORTED_NAMES = { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT };
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("(?<x>\\d+),(?<y>\\d+)");

    private final String value;

    TransparentPixel(final String value) {
        this.value = Objects.requireNonNull(value, getClass().getSimpleName() + ".value cannot be null!");
    }

    public static void validate(final String value) {
        if (value == null
            || (!Arrays.stream(SUPPORTED_NAMES)
                    .anyMatch(name -> Strings.CI.equals(name, value))
                && !COORDINATE_PATTERN.matcher(value).matches())) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.INVALID_TRANSPARENT_PIXEL,
                    "Value '"
                                                         + value
                                                         + "' is not in the valid names '"
                                                         + Arrays.stream(SUPPORTED_NAMES)
                                                                 .collect(Collectors.joining(", "))
                                                         + "' nor does it match the pattern '"
                                                         + COORDINATE_PATTERN.pattern()
                                                         + "' (e.g. '0, 125' with 0 being the horinzontal and 125 being the vertical coordinate).");
        }
    }

    public Color getColorOfPixel(final BufferedImage image) {
        Objects.requireNonNull(image);
        final Coordinate pixelCoordinate = getCoordinate(image);
        return new Color(image.getRGB(pixelCoordinate.x(), pixelCoordinate.y()), true);
    }

    private Coordinate getCoordinate(final BufferedImage image) {
        if (Strings.CI.equals(TOP_LEFT, value)) {
            return new Coordinate(0, 0);
        }
        if (Strings.CI.equals(TOP_RIGHT, value)) {
            return new Coordinate(image.getWidth() - 1, 0);
        }
        if (Strings.CI.equals(BOTTOM_LEFT, value)) {
            return new Coordinate(0, image.getHeight() - 1);
        }
        if (Strings.CI.equals(BOTTOM_RIGHT, value)) {
            return new Coordinate(image.getWidth() - 1, image.getHeight() - 1);
        }
        final Matcher matcher = COORDINATE_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Value '" + value + "' doesn ot match pattern '" + COORDINATE_PATTERN.pattern() + "'.");
        }

        final int x = Integer.parseInt(matcher.group("x"));
        final int y = Integer.parseInt(matcher.group("y"));
        return new Coordinate(x, y);
    }

    private record Coordinate(int x, int y) {

    }

}
