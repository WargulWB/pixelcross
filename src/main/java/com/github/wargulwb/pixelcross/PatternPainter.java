package com.github.wargulwb.pixelcross;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.model.PixelCross;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

import jakarta.inject.Inject;

public class PatternPainter extends AbstractPainter {

    private static final Color LIGHT_GRID_LINE_COLOR = Color.GRAY;
    private static final Color MID_GRID_LINE_COLOR = Color.DARK_GRAY;
    private static final Color DARK_GRID_LINE_COLOR = Color.BLACK;
    private static final int GRID_LINE_WIDTH = 1;
    private static final int LABEL_AREA_WIDTH = 50;
    private static final int LABEL_FONT_WIDTH = 5;

    @Inject
    public PatternPainter(final ColorUtils colorUtils, final PixelCrossConfig config) {
        super(colorUtils, config);
    }

    @Override
    public BufferedImage paint(final ImageModel imageModel) {
        final int imageWidth = calculateWidth(imageModel);
        final int imageHeight = calculateHeight(imageModel);

        final BufferedImage paintedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        paint(paintedImage, imageModel);
        return paintedImage;
    }

    private void paint(final BufferedImage image, final ImageModel imageModel) {
        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(BACKGROUND);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintGrid(graphics, image, imageModel);
        paintLabels(graphics, image, imageModel);
        paintCrosses(graphics, imageModel);
    }

    private void paintCrosses(final Graphics2D graphics, final ImageModel imageModel) {
        for (int x = 0; x < imageModel.getWidth(); x++) {
            for (int y = 0; y < imageModel.getHeight(); y++) {
                final int xPos = LABEL_AREA_WIDTH + x * BLOCK_SIZE + (x + 1) * GRID_LINE_WIDTH;
                final int yPos = LABEL_AREA_WIDTH + y * BLOCK_SIZE + (y + 1) * GRID_LINE_WIDTH;
                final PixelCross pixelCross = imageModel.getPixelCross(x, y);
                if (pixelCross.getPixelColor().isPixelOpaque()) { // only draw full opaque pixels
                    paintCross(graphics, xPos, yPos, imageModel, pixelCross.getPixelColor().getYarn());
                }
            }
        }
    }

    private void paintLabels(final Graphics2D graphics, final BufferedImage image, final ImageModel imageModel) {
        graphics.setColor(DARK_GRID_LINE_COLOR);
        // horizontal labels
        for (int i = 0; i < imageModel.getWidth() + 1; i++) {
            if (i % 5 != 0) {
                continue;
            }
            final String labelText = Integer.toString(i);
            final int labelOffset = labelText.length() * LABEL_FONT_WIDTH;
            final int x = LABEL_AREA_WIDTH + i * BLOCK_SIZE + i * GRID_LINE_WIDTH - labelOffset;

            graphics.drawString(labelText, x, (LABEL_AREA_WIDTH / 4) * 3);
            graphics.drawString(labelText, x, image.getHeight() - (LABEL_AREA_WIDTH / 4) * 3);
        }
        // vertical labels
        for (int i = 0; i < imageModel.getHeight() + 1; i++) {
            if (i % 5 != 0) {
                continue;
            }
            final String labelText = Integer.toString(i);
            final int y = LABEL_AREA_WIDTH + FONT_SIZE / 2 + i * BLOCK_SIZE + i * GRID_LINE_WIDTH;
            final int leftLabelHorizontalOffset = (labelText.length() - 2) * LABEL_FONT_WIDTH;
            graphics.drawString(labelText, LABEL_AREA_WIDTH / 2 - leftLabelHorizontalOffset, y);
            graphics.drawString(labelText, image.getWidth() - LABEL_AREA_WIDTH + 2, y);
        }
    }

    private void paintGrid(final Graphics2D graphics, final BufferedImage image, final ImageModel imageModel) {
        // vertical lines
        for (int i = 0; i < imageModel.getWidth() + 1; i++) {
            graphics.setColor(pickGridLineColor(i));
            final int x = LABEL_AREA_WIDTH + i * BLOCK_SIZE + i * GRID_LINE_WIDTH;
            // -1 because otherwise the rectangle would be a pixel wider
            graphics.drawRect(x, LABEL_AREA_WIDTH, GRID_LINE_WIDTH - 1, image.getHeight() - LABEL_AREA_WIDTH * 2);
        }
        // horizontal lines
        for (int i = 0; i < imageModel.getHeight() + 1; i++) {
            graphics.setColor(pickGridLineColor(i));
            final int y = LABEL_AREA_WIDTH + i * BLOCK_SIZE + i * GRID_LINE_WIDTH;
            // -1 because otherwise the rectangle would be a pixel wider
            graphics.drawRect(LABEL_AREA_WIDTH, y, image.getWidth() - LABEL_AREA_WIDTH * 2, GRID_LINE_WIDTH - 1);
        }
    }

    private Color pickGridLineColor(final int lineIndex) {
        if (lineIndex % 10 == 0) {
            return DARK_GRID_LINE_COLOR;
        }
        if (lineIndex % 5 == 0) {
            return MID_GRID_LINE_COLOR;
        }
        return LIGHT_GRID_LINE_COLOR;
    }

    private int calculateWidth(final ImageModel imageModel) {
        // each block represents one original pixel with a grid line between 2 crosses and 2 outer grid lines
        // additionally labels are added on both sides
        return imageModel.getWidth() * BLOCK_SIZE + (imageModel.getWidth() + 1) * GRID_LINE_WIDTH + LABEL_AREA_WIDTH * 2;
    }

    private int calculateHeight(final ImageModel imageModel) {
        // each block represents one original pixel with a grid line between 2 crosses and 2 outer grid lines
        // additionally labels are added on both sides
        return imageModel.getHeight() * BLOCK_SIZE + (imageModel.getHeight() + 1) * GRID_LINE_WIDTH + LABEL_AREA_WIDTH * 2;
    }

}
