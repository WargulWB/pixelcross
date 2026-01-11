package com.github.wargulwb.pixelcross;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
    private static final Color CENTER_LINE_COLOR = new Color(77, 77, 245); // #4D4DF5 blue
    private static final int GRID_LINE_WIDTH = 1;
    private static final int LABEL_AREA_WIDTH = 50;
    private static final int LABEL_FONT_WIDTH = 5;
    private static final int CENTER_INDICATOR_HEIGHT = 10;
    private static final int CENTER_INDICATOR_WIDTH = 10;
    private static final int CENTER_CIRCLE_RADIUS = 4;

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
        paintCenterMark(graphics, imageModel);
    }

    /**
     * Marks the line in the middle via triangle indicators. The middle is the line between the center pixels/crosses of the image meaning
     * if the image has 64 pixels the line after pixel 32 is indicated. For images with uneven pixel count, the center pixel would be
     * floor(length / 2) meaning if it is a 3x3 pixel image the line after the 1st pixel is marked.
     *
     * @param graphics
     * @param imageModel
     */
    private void paintCenterMark(final Graphics2D graphics, final ImageModel imageModel) {
        final int modelCenterX = imageModel.getWidth() / 2;

        // upper marker
        final int positionCenterX = LABEL_AREA_WIDTH + modelCenterX * BLOCK_SIZE + (modelCenterX) * GRID_LINE_WIDTH;
        final int outlineTopY = LABEL_AREA_WIDTH;

        graphics.setColor(DARK_GRID_LINE_COLOR);
        final Polygon upperIndicator = new Polygon();
        upperIndicator.addPoint(positionCenterX, outlineTopY);
        upperIndicator.addPoint(positionCenterX - CENTER_INDICATOR_WIDTH / 2, outlineTopY - CENTER_INDICATOR_HEIGHT);
        upperIndicator.addPoint(positionCenterX + CENTER_INDICATOR_WIDTH / 2, outlineTopY - CENTER_INDICATOR_HEIGHT);
        graphics.fillPolygon(upperIndicator);

        // lower marker
        final int outlineBottomY = LABEL_AREA_WIDTH + imageModel.getHeight() * BLOCK_SIZE + (imageModel.getHeight()) * GRID_LINE_WIDTH;

        graphics.setColor(DARK_GRID_LINE_COLOR);
        final Polygon lowerIndicator = new Polygon();
        lowerIndicator.addPoint(positionCenterX, outlineBottomY);
        lowerIndicator.addPoint(positionCenterX - CENTER_INDICATOR_WIDTH / 2, outlineBottomY + CENTER_INDICATOR_HEIGHT);
        lowerIndicator.addPoint(positionCenterX + CENTER_INDICATOR_WIDTH / 2, outlineBottomY + CENTER_INDICATOR_HEIGHT);
        graphics.fillPolygon(lowerIndicator);

        // left marker
        final int modelCenterY = imageModel.getHeight() / 2;
        final int positionCenterY = LABEL_AREA_WIDTH + modelCenterY * BLOCK_SIZE + (modelCenterY) * GRID_LINE_WIDTH;
        final int outlineLeftX = LABEL_AREA_WIDTH;
        final Polygon leftIndicator = new Polygon();
        leftIndicator.addPoint(outlineLeftX, positionCenterY);
        leftIndicator.addPoint(outlineLeftX - CENTER_INDICATOR_HEIGHT, positionCenterY - CENTER_INDICATOR_WIDTH / 2);
        leftIndicator.addPoint(outlineLeftX - CENTER_INDICATOR_HEIGHT, positionCenterY + CENTER_INDICATOR_WIDTH / 2);
        graphics.fillPolygon(leftIndicator);

        // right marker
        final int outlineRightX = LABEL_AREA_WIDTH + imageModel.getWidth() * BLOCK_SIZE + (imageModel.getWidth()) * GRID_LINE_WIDTH;
        final Polygon rightIndicator = new Polygon();
        rightIndicator.addPoint(outlineRightX, positionCenterY);
        rightIndicator.addPoint(outlineRightX + CENTER_INDICATOR_HEIGHT, positionCenterY - CENTER_INDICATOR_WIDTH / 2);
        rightIndicator.addPoint(outlineRightX + CENTER_INDICATOR_HEIGHT, positionCenterY + CENTER_INDICATOR_WIDTH / 2);
        graphics.fillPolygon(rightIndicator);

        graphics.setColor(CENTER_LINE_COLOR);

        // vertical line
        graphics.drawLine(positionCenterX, outlineTopY, positionCenterX, outlineBottomY);

        // horizontal line
        graphics.drawLine(outlineLeftX, positionCenterY, outlineRightX, positionCenterY);

        graphics.drawOval(positionCenterX - CENTER_CIRCLE_RADIUS,
                positionCenterY - CENTER_CIRCLE_RADIUS,
                CENTER_CIRCLE_RADIUS * 2,
                CENTER_CIRCLE_RADIUS * 2);
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
            graphics.drawString(labelText, x, image.getHeight() - (LABEL_AREA_WIDTH / 4) * 3 + CENTER_INDICATOR_HEIGHT);
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
            graphics.drawString(labelText, image.getWidth() - LABEL_AREA_WIDTH + 2 + CENTER_INDICATOR_HEIGHT, y);
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
