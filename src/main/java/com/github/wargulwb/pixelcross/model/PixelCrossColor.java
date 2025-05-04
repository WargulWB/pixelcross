package com.github.wargulwb.pixelcross.model;

import java.awt.Color;
import java.util.Objects;

import com.github.wargulwb.pixelcross.model.yarn.Yarn;

public class PixelCrossColor {

    private final Color pixelColor;
    private Yarn yarn; // unfortunately modifiable

    public PixelCrossColor(final Color color) {
        pixelColor = Objects.requireNonNull(color, "Parameter 'color' cannot be null!");
    }

    public Color getOriginalColor() {
        return pixelColor;
    }

    public Yarn getYarn() {
        return yarn;
    }

    public PixelCrossColor setYarn(final Yarn yarn) {
        this.yarn = yarn;
        return this;
    }

    public boolean isPixelOpaque() {
        return pixelColor.getAlpha() == 255;
    }

}
