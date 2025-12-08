package com.github.wargulwb.pixelcross.integrationtest;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.imageio.ImageIO;

class ImageComparator {

    void compare(final Path expectedImagePath, final Path actualImagePath) throws IOException {
        final BufferedImage expectedImage = ImageIO.read(expectedImagePath.toFile());
        final BufferedImage actualImage = ImageIO.read(actualImagePath.toFile());

        final int expectedWidth = expectedImage.getWidth();
        final int actualWidth = actualImage.getWidth();

        final int expectedHeight = expectedImage.getHeight();
        final int actualHeight = actualImage.getHeight();

        if (expectedWidth != actualWidth || expectedHeight != actualHeight) {
            throw new AssertionError(
                    "Dimension [" + expectedWidth + "," + expectedHeight + "] of expected image ('" + expectedImagePath +
                                     ") does not match dimension [" + actualWidth + "," + actualHeight + "] of actual image ('"
                                     + actualImagePath + ").");
        }

        for (int x = 0; x < expectedWidth; x++) {
            for (int y = 0; y < expectedHeight; y++) {
                final Color expectedPixelColor = new Color(expectedImage.getRGB(x, y), true);
                final Color actualPixelColor = new Color(actualImage.getRGB(x, y), true);
                if (!Objects.equals(expectedPixelColor, actualPixelColor)) {
                    throw new AssertionError(
                            "Expected image '" + expectedImagePath + "' and actual image '" + actualImagePath
                                             + "' differ in the color of pixel [" + x + "," + y
                                             + "]. Color of pixel of expected image is: '" + expectedPixelColor
                                             + "'. Color of pixel of actual image is: '" + actualPixelColor
                                             + "'.");
                }
            }
        }

    }

}
