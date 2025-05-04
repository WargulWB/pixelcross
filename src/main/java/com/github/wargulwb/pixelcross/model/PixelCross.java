package com.github.wargulwb.pixelcross.model;

import java.util.Objects;

public class PixelCross {

    private final PixelCrossColor pixelColor;

    public PixelCross(final PixelCrossColor color) {
        pixelColor = Objects.requireNonNull(color, "Parameter 'color' cannot be null!");
    }

    public PixelCrossColor getPixelColor() {
        return pixelColor;
    }

}
