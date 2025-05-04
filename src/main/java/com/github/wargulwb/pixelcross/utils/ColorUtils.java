package com.github.wargulwb.pixelcross.utils;

import java.awt.Color;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ColorUtils {

    public Color colorFromRGBHexString(final String rgbHexNoAlpha) {
        if (rgbHexNoAlpha.length() != 7) {
            throw new IllegalArgumentException("Hex string '" + rgbHexNoAlpha + "' is not the expected 7 characters '#012345' long!");
        }
        final int red = Integer.valueOf(rgbHexNoAlpha.substring(1, 3), 16);
        final int green = Integer.valueOf(rgbHexNoAlpha.substring(3, 5), 16);
        final int blue = Integer.valueOf(rgbHexNoAlpha.substring(5, 7), 16);
        return new Color(red, green, blue);
    }

    public String colorToRGBHexString(final Color color) {
        final String red = StringUtils.leftPad(Integer.toHexString(color.getRed()), 2, '0');
        final String green = StringUtils.leftPad(Integer.toHexString(color.getGreen()), 2, '0');
        final String blue = StringUtils.leftPad(Integer.toHexString(color.getBlue()), 2, '0');
        return "#" + red + green + blue;
    }

    public double distanceRGB(final Color color1, final Color color2) {
        final int redDistance = Math.abs(color1.getRed() - color2.getRed());
        final int greenDistance = Math.abs(color1.getGreen() - color2.getGreen());
        final int blueDistance = Math.abs(color1.getBlue() - color2.getBlue());//
        return Math.sqrt(redDistance * redDistance + greenDistance * greenDistance + blueDistance * blueDistance);
    }

    public float[] toHSB(final Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }

    public int compareByHueThanBrightnessThanSaturation(final Color color1, final Color color2) {
        final float[] hsb1 = toHSB(color1);
        final float[] hsb2 = toHSB(color2);
        int comp = Float.compare(hsb1[0], hsb2[0]);
        if (comp != 0) {
            return comp;
        }
        comp = Float.compare(hsb1[2], hsb2[2]);
        if (comp != 0) {
            return comp;
        }
        return Float.compare(hsb1[1], hsb2[1]);
    }

    // public Color getClosestNeighbor(final Color color, final List<Color> colors) {
    // if (colors == null || colors.isEmpty()) {
    // throw new IllegalArgumentException("Given 'colors' must not be null or empty!");
    // }
    // Color closestNeighbor = null; // never effectively null on return
    // int closestDistance = Integer.MAX_VALUE;
    // for (final Color otherColor : colors) {
    // final int distance = distanceRGB(color, otherColor);
    // if (distance < closestDistance) {
    // closestNeighbor = otherColor;
    // closestDistance = distance;
    // }
    // }
    // return closestNeighbor;
    // }

    public <T extends ColorHolder> T getClosestNeighborByColor(final Color color, final List<T> colorHolders) {
        if (colorHolders == null || colorHolders.isEmpty()) {
            throw new IllegalArgumentException("Given 'colorHolders' must not be null or empty!");
        }
        T closestNeighbor = null; // never effectively null on return
        double closestDistance = Double.MAX_VALUE;
        for (final T colorHolder : colorHolders) {
            final double distance = distanceRGB(color, colorHolder.getColor());
            if (distance < closestDistance) {
                closestNeighbor = colorHolder;
                closestDistance = distance;
            }
        }
        return closestNeighbor;
    }

    public Color toGrayScale(final Color color) {
        final float[] hsb = toHSB(color);
        return new Color(Color.HSBtoRGB(hsb[0], 0, hsb[2]));
    }

}
