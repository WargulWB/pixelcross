package com.github.wargulwb.pixelcross;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO remove later
 *
 * This is just a help script to read colors from an image, because I need some way to get the rgb value of a color table.
 */
public class ColorReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColorReader.class);

    public static void main(final String[] args) throws IOException {
        LOGGER.debug("'pixelcross' was called with arguments: '{}'",
                Arrays.stream(args).collect(Collectors.joining("', '")));
        final Path colorImagePath = Paths.get(args[0]);
        final BufferedImage image = ImageIO.read(colorImagePath.toFile());
        readColors(image, 80, 16, 1);
    }

    private static void readColors(final BufferedImage image, final int cellWidth, final int cellHeight, final int lineWidth) {
        // validate image format, expecting table image with cell lines before and after each cell (including first and last cell)
        final int width = image.getWidth();
        final int height = image.getHeight();
        if ((width - lineWidth) % (cellWidth + lineWidth) > 0) {
            throw new IllegalArgumentException("Image is of invalid format, width is '" + width + "' and not dividable without rest!");
        }
        if ((height - lineWidth) % (cellHeight + lineWidth) > 0) {
            throw new IllegalArgumentException("Image is of invalid format, height is '" + height + "' and not dividable without rest!");
        }
        final int centerOffsetX = cellWidth / 2 + lineWidth;
        final int centerOffsetY = cellHeight / 2 + lineWidth;
        int index = 1;
        for (int x = lineWidth; x < image.getWidth(); x += cellWidth + lineWidth) {
            // run through rows and switches to next column afterwards
            for (int y = lineWidth; y < image.getHeight(); y += cellHeight + lineWidth) {
                final Color color = new Color(image.getRGB(x + centerOffsetX, y + centerOffsetY), false);
                LOGGER.info("{}: {}", StringUtils.leftPad(Integer.toString(index), 4, ' '), asRGBHex(color));
                index++;
            }
        }
    }

    private static String asRGBHex(final Color color) {
        final String red = StringUtils.leftPad(Integer.toHexString(color.getRed()), 2, '0');
        final String green = StringUtils.leftPad(Integer.toHexString(color.getGreen()), 2, '0');
        final String blue = StringUtils.leftPad(Integer.toHexString(color.getBlue()), 2, '0');
        return "#" + red + green + blue;
    }

}
