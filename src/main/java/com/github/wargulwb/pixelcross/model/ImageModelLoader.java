package com.github.wargulwb.pixelcross.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;
import com.github.wargulwb.pixelcross.model.yarn.Yarn;
import com.github.wargulwb.pixelcross.model.yarn.YarnSortiment;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

import jakarta.inject.Inject;

public class ImageModelLoader {

    private final PixelCrossConfig config;
    private final ColorUtils colorUtils;

    @Inject
    ImageModelLoader(final PixelCrossConfig config, final ColorUtils colorUtils) {
        this.config = Objects.requireNonNull(config, getClass().getSimpleName() + ".config cannot be null!");
        this.colorUtils = Objects.requireNonNull(colorUtils, getClass().getSimpleName() + ".colorUtils cannot be null!");
    }

    public ImageModel loadImageModel(final Path imageFile) {
        Objects.requireNonNull("Parameter 'imageFile' may not be null!");
        final BufferedImage image = loadBufferedImage(imageFile);
        final ImageModel imageModel = convertToImageModel(image);
        addYarnData(imageModel);
        return imageModel;
    }

    private void addYarnData(final ImageModel imageModel) {
        imageModel.getColors().forEach(this::addYarnData);
        imageModel.updateYarnIndexMap();
    }

    private void addYarnData(final PixelCrossColor color) {
        color.setYarn(pickClosestYarn(color)); // TODO solve the issue of two colors falling into the same yarn color aka color reduction
    }

    private Yarn pickClosestYarn(final PixelCrossColor color) {
        // TODO later on, one might likely wants a way to restrict the sortiment
        if (!color.isPixelOpaque()) { // skip pixels with transparency
            return null;
        }
        final List<Yarn> allYarns = config.getYarnSortiments()
                .stream()
                .map(YarnSortiment::getYarns)
                .flatMap(Collection::stream)
                .toList();
        return colorUtils.getClosestNeighborByColor(color.getOriginalColor(), allYarns);
    }

    private ImageModel convertToImageModel(final BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        if (width == 1 || height == 1) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.INVALID_IMAGE_FORMAT,
                    "Image must have a width and height greater than 1 pixel, but dimension were (w:'" + width + "',h:'" + height + "')");
        }
        final PixelCross[][] grid = new PixelCross[width][height];
        final Map<Color, PixelCrossColor> colorMemory = new HashMap<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                final Color color = new Color(image.getRGB(x, y), true);
                final PixelCrossColor pixelCrossColor;
                if (!colorMemory.containsKey(color)) {
                    pixelCrossColor = new PixelCrossColor(color);
                    colorMemory.put(color, pixelCrossColor);
                } else {
                    pixelCrossColor = colorMemory.get(color);
                }
                grid[x][y] = new PixelCross(pixelCrossColor);
            }
        }
        final List<PixelCrossColor> colors = colorMemory.values().stream().distinct().toList();
        return new ImageModel(width, height, grid, colors);
    }

    private BufferedImage loadBufferedImage(final Path imageFile) {
        try {
            return ImageIO.read(imageFile.toFile());
        } catch (final IOException exc) {
            throw new UncheckedIOException("Failed to load image '" + imageFile + "'.", exc);
        }
    }

}
