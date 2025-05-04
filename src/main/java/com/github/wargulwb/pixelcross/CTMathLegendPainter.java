package com.github.wargulwb.pixelcross;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

import jakarta.inject.Inject;

public class CTMathLegendPainter extends AbstractPainter {

    private static final double INCH_TO_CM = 2.54D;
    private static final DecimalFormat FORMAT = new DecimalFormat(
            "#,###.##",
            new DecimalFormatSymbols(Locale.US));

    @Inject
    public CTMathLegendPainter(final ColorUtils colorUtils, final PixelCrossConfig config) {
        super(colorUtils, config);
    }

    @Override
    public BufferedImage paint(final ImageModel imageModel) {
        final List<String> calculations = getCalculationStrings(imageModel);
        final int width = calculateWidth(calculations);
        final int height = calculateHeight(calculations);

        final BufferedImage paintedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintCalculations((Graphics2D) paintedImage.getGraphics(), paintedImage, calculations);
        return paintedImage;
    }

    private int calculateHeight(final List<String> calculations) {
        return (calculations.size() + 1) * FONT_SIZE + Math.max((calculations.size() - 1) * LEGEND_PADDING, 0);
    }

    private int calculateWidth(final List<String> calculations) {
        return calculations
                .stream()
                // I do not know the width of a character, using the AVG_CHAR_WIDTH as an approximation which is "big enough"
                .map(calc -> calc.length() * AVG_CHAR_WIDTH)
                .sorted((y1, y2) -> Integer.compare(y2, y1))// highest first
                .findFirst()
                .orElse(100);
    }

    private void paintCalculations(final Graphics2D graphics, final BufferedImage image, final List<String> calculations) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(BACKGROUND);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(TEXT_COLOR);

        int yPos = 0;
        for (final String entry : calculations) {
            paintEntry(graphics, yPos, entry);
            yPos += BLOCK_SIZE + LEGEND_PADDING;
        }
    }

    private void paintEntry(final Graphics2D graphics, final int yPos, final String entry) {
        final int xOffset = 10;
        graphics.setColor(TEXT_COLOR);
        graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        graphics.drawString(entry,
                xOffset + BLOCK_SIZE + LEGEND_PADDING,
                yPos + FONT_SIZE);
    }

    private List<String> getCalculationStrings(final ImageModel imageModel) {
        final int outerLeftPaintedPixel = getOuterLeftPaintedPixel(imageModel);
        final int outerRightPaintedPixel = getOuterRightPaintedPixel(imageModel);
        final int upperMostPaintedPixel = getUpperMostPaintedPixel(imageModel);
        final int lowerMostPaintedPixel = getLowerMostPaintedPixel(imageModel);

        // origin upper left
        final int pixelWidth = outerRightPaintedPixel - outerLeftPaintedPixel + 1;
        final int pixelHeight = lowerMostPaintedPixel - upperMostPaintedPixel + 1;

        final List<String> calculations = new ArrayList<>();
        calculations.add("CT := crosses per inch, 1 inch = 2.54cm");
        calculations.add("effective dimension (only counting painted pixels): " + pixelWidth + "px x " + pixelHeight + "px");
        calculations.add(getCalculation(12, pixelWidth, pixelHeight));
        calculations.add(getCalculation(14, pixelWidth, pixelHeight));
        calculations.add(getCalculation(16, pixelWidth, pixelHeight));
        calculations.add(getCalculation(18, pixelWidth, pixelHeight));

        return calculations;
    }

    private String getCalculation(final int ct, final int pixelWidth, final int pixelHeight) {
        final double widthInches = pixelWidth / (double) ct;
        final double heightInches = pixelHeight / (double) ct;
        return new StringBuilder()
                .append(ct)
                .append("CT | ")
                .append(FORMAT.format(widthInches))
                .append("in x ")
                .append(FORMAT.format(heightInches))
                .append("in | ")
                .append(FORMAT.format(widthInches * INCH_TO_CM))
                .append("cm x ")
                .append(FORMAT.format(heightInches * INCH_TO_CM))
                .append("cm")
                .toString();
    }

    private int getOuterLeftPaintedPixel(final ImageModel imageModel) {
        final int width = imageModel.getWidth();
        final int height = imageModel.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (imageModel.getPixelCross(x, y).getPixelColor().isPixelOpaque()) {
                    return x;
                }
            }
        }

        throw new IllegalStateException("Found no outer left painted pixel!");
    }

    private int getOuterRightPaintedPixel(final ImageModel imageModel) {
        final int width = imageModel.getWidth();
        final int height = imageModel.getHeight();

        for (int x = width - 1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                if (imageModel.getPixelCross(x, y).getPixelColor().isPixelOpaque()) {
                    return x;
                }
            }
        }

        throw new IllegalStateException("Found no outer right painted pixel!");
    }

    private int getUpperMostPaintedPixel(final ImageModel imageModel) {
        final int width = imageModel.getWidth();
        final int height = imageModel.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (imageModel.getPixelCross(x, y).getPixelColor().isPixelOpaque()) {
                    return y;
                }
            }
        }

        throw new IllegalStateException("Found no upper most painted pixel!");
    }

    private int getLowerMostPaintedPixel(final ImageModel imageModel) {
        final int width = imageModel.getWidth();
        final int height = imageModel.getHeight();

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (imageModel.getPixelCross(x, y).getPixelColor().isPixelOpaque()) {
                    return y;
                }
            }
        }

        throw new IllegalStateException("Found no lower most painted pixel!");
    }

}
