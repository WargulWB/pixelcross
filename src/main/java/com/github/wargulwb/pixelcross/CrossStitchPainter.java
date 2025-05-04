package com.github.wargulwb.pixelcross;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.model.PixelCross;

public class CrossStitchPainter implements Painter {

    private static final Color BACKGROUND = new Color(255, 245, 225); // yellowish white
    private static final Color GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private static final int GRID_LINE_WIDTH = 1;
    private static final int CROSS_SIZE = 10;

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
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight()); // background
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintGrid(graphics, image, imageModel);
        paintCrosses(graphics, imageModel);
    }

    private void paintCrosses(final Graphics2D graphics, final ImageModel imageModel) {
        for (int x = 0; x < imageModel.getWidth(); x++) {
            for (int y = 0; y < imageModel.getHeight(); y++) {
                final int xPos = x * CROSS_SIZE + (x + 1) * GRID_LINE_WIDTH;
                final int yPos = y * CROSS_SIZE + (y + 1) * GRID_LINE_WIDTH;
                final PixelCross pixelCross = imageModel.getPixelCross(x, y);
                if (pixelCross.getPixelColor().isPixelOpaque()) { // only draw full opaque pixels
                    graphics.setColor(pixelCross.getPixelColor().getYarn().getColor());
                    drawWideCross(graphics, xPos, yPos);
                    // fillCrossAsRect(graphics, xPos, yPos);
                    graphics.setColor(BACKGROUND);
                }
            }
        }
    }

    private void drawNarrowCross(final Graphics2D graphics, final int xPos, final int yPos) {
        graphics.drawLine(xPos, yPos, xPos + CROSS_SIZE, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + CROSS_SIZE, yPos, xPos, yPos + CROSS_SIZE);
    }

    private void drawWideCross(final Graphics2D graphics, final int xPos, final int yPos) {
        graphics.drawLine(xPos, yPos, xPos + CROSS_SIZE, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + 1, yPos, xPos + CROSS_SIZE, yPos + CROSS_SIZE - 1);
        graphics.drawLine(xPos + 2, yPos, xPos + CROSS_SIZE, yPos + CROSS_SIZE - 2);
        graphics.drawLine(xPos, yPos + 1, xPos + CROSS_SIZE - 1, yPos + CROSS_SIZE);
        graphics.drawLine(xPos, yPos + 2, xPos + CROSS_SIZE - 2, yPos + CROSS_SIZE);
        graphics.drawLine(xPos, yPos + 3, xPos + CROSS_SIZE - 3, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + CROSS_SIZE, yPos, xPos, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + CROSS_SIZE - 1, yPos, xPos, yPos + CROSS_SIZE - 1);
        graphics.drawLine(xPos + CROSS_SIZE, yPos + 1, xPos + 1, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + CROSS_SIZE - 2, yPos, xPos, yPos + CROSS_SIZE - 2);
        graphics.drawLine(xPos + CROSS_SIZE, yPos + 2, xPos + 2, yPos + CROSS_SIZE);
        graphics.drawLine(xPos + CROSS_SIZE, yPos + 3, xPos + 3, yPos + CROSS_SIZE);
    }

    private void fillCrossAsRect(final Graphics2D graphics, final int xPos, final int yPos) {
        graphics.fillRect(xPos, yPos, CROSS_SIZE, CROSS_SIZE);
    }

    private void paintGrid(final Graphics2D graphics, final BufferedImage image, final ImageModel imageModel) {
        graphics.setColor(GRID_LINE_COLOR);

        // vertical lines
        for (int i = 0; i < imageModel.getWidth() + 1; i++) {
            final int x = i * CROSS_SIZE + i * GRID_LINE_WIDTH;
            // -1 because otherwise the rectangle would be a pixel wider
            graphics.drawRect(x, 0, GRID_LINE_WIDTH - 1, image.getHeight());
        }
        // horizontal lines
        for (int i = 0; i < imageModel.getHeight() + 1; i++) {
            final int y = i * CROSS_SIZE + i * GRID_LINE_WIDTH;
            // -1 because otherwise the rectangle would be a pixel wider
            graphics.drawRect(0, y, image.getWidth(), GRID_LINE_WIDTH - 1);
        }
    }

    private int calculateWidth(final ImageModel imageModel) {
        // each cross represents one original pixel with a grid line between 2 crosses and 2 outer grid lines
        return imageModel.getWidth() * CROSS_SIZE + (imageModel.getWidth() + 1) * GRID_LINE_WIDTH;
    }

    private int calculateHeight(final ImageModel imageModel) {
        // each cross represents one original pixel with a grid line between 2 crosses and 2 outer grid lines
        return imageModel.getHeight() * CROSS_SIZE + (imageModel.getHeight() + 1) * GRID_LINE_WIDTH;
    }

}
