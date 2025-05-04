package com.github.wargulwb.pixelcross;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.model.yarn.Yarn;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

import jakarta.inject.Inject;

public class YarnLegendPainter extends AbstractPainter {

    private static final int LEGEND_LEFT_PAD_CROSS_COUNT = 6;
    private static final int LEGEND_LEFT_PAD_ID = 5;

    @Inject
    public YarnLegendPainter(final ColorUtils colorUtils, final PixelCrossConfig config) {
        super(colorUtils, config);
    }

    @Override
    public BufferedImage paint(final ImageModel imageModel) {
        final int width = calculateLegendWidth(imageModel);
        final int height = calculateLegendHeight(imageModel);

        final BufferedImage paintedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        paintLegend((Graphics2D) paintedImage.getGraphics(), paintedImage, imageModel);
        return paintedImage;
    }

    private int calculateLegendHeight(final ImageModel imageModel) {
        final int colorCount = imageModel.getEffectiveColorCount() + 1;
        return colorCount * BLOCK_SIZE + (colorCount - 1) * LEGEND_PADDING;
    }

    private void paintLegend(final Graphics2D graphics, final BufferedImage image, final ImageModel imageModel) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(BACKGROUND);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(TEXT_COLOR);

        final List<Yarn> yarns = imageModel.getYarns()
                .stream()
                .sorted(this::orderByColor)
                .toList();
        int yPos = 0;
        for (final Yarn yarn : yarns) {
            paintLegendEntry(graphics, yPos, imageModel, yarn);
            yPos += BLOCK_SIZE + LEGEND_PADDING;
        }
    }

    private void paintLegendEntry(final Graphics2D graphics, final int yPos, final ImageModel imageModel, final Yarn yarn) {
        final int xOffset = 10;
        paintCross(graphics, xOffset, yPos, imageModel, yarn);
        graphics.setColor(TEXT_COLOR);
        graphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));
        graphics.drawString(getLegendEntry(imageModel, yarn),
                xOffset + BLOCK_SIZE + LEGEND_PADDING,
                yPos + FONT_SIZE);
    }

    private String getLegendEntry(final ImageModel imageModel, final Yarn yarn) {
        final StringBuilder sb = new StringBuilder()
                .append(StringUtils.leftPad("x" + imageModel.getYarnCrossCount(yarn), LEGEND_LEFT_PAD_CROSS_COUNT, ' '))
                .append(" | ")
                .append(yarn.getYarnSortiment().getProducer())
                .append(" | ")
                .append("[")
                .append(StringUtils.leftPad(yarn.getId(), LEGEND_LEFT_PAD_ID, ' '))
                .append("] ")
                .append(yarn.getName());
        return sb.toString();
    }

    private final int orderByColor(final Yarn yarn1, final Yarn yarn2) {
        return colorUtils.compareByHueThanBrightnessThanSaturation(yarn1.getColor(), yarn2.getColor());
    }

    private int calculateLegendWidth(final ImageModel imageModel) {
        return imageModel.getYarns()
                .stream()
                // I do not know the width of a character, using the AVG_CHAR_WIDTH as an approximation which is "big enough"
                .map(y -> getLegendEntry(imageModel, y).length() * AVG_CHAR_WIDTH)
                .sorted((y1, y2) -> Integer.compare(y2, y1))// highest first
                .findFirst()
                .orElse(100);
    }

}
