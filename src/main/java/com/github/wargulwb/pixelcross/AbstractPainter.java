package com.github.wargulwb.pixelcross;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;
import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.model.yarn.Yarn;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

public abstract class AbstractPainter implements Painter {

    protected static final Color BACKGROUND = Color.WHITE;
    protected static final int BLOCK_SIZE = 20;
    protected static final int FONT_SIZE = 18;
    protected static final int AVG_CHAR_WIDTH = 14;
    protected static final Color SYMBOL_COLOR = Color.BLACK;
    protected static final Color SYMBOL_COLOR_INVERSE = Color.WHITE;
    protected static final Color TEXT_COLOR = Color.BLACK;
    protected static final int LEGEND_PADDING = 2;

    protected final ColorUtils colorUtils;
    protected final PixelCrossConfig config;

    protected AbstractPainter(final ColorUtils colorUtils, final PixelCrossConfig config) {
        this.colorUtils = Objects.requireNonNull(colorUtils, getClass().getSimpleName() + ".colorUtils cannot be null!");
        this.config = Objects.requireNonNull(config, getClass().getSimpleName() + ".config cannot be null!");
    }

    protected void paintCross(final Graphics2D graphics, final int xPos, final int yPos, final ImageModel imageModel, final Yarn yarn) {
        final Color color = config.isOutputGrayscale() ? colorUtils.toGrayScale(yarn.getColor()) : yarn.getColor();
        graphics.setColor(color);
        fillCrossAsRect(graphics, xPos, yPos);
        paintSymbol(graphics, xPos, yPos, imageModel, yarn, getSymbolColor(color));
    }

    private Color getSymbolColor(final Color backgroundColor) {
        final int green = backgroundColor.getGreen();
        final int blue = backgroundColor.getBlue();
        final int red = backgroundColor.getRed();
        final int threshold = 90;
        return (green + blue + red) / 3 > threshold ? SYMBOL_COLOR : SYMBOL_COLOR_INVERSE;
    }

    private void fillCrossAsRect(final Graphics2D graphics, final int xPos, final int yPos) {
        graphics.fillRect(xPos, yPos, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void paintSymbol(final Graphics2D graphics,
                             final int xPos,
                             final int yPos,
                             final ImageModel imageModel,
                             final Yarn yarn,
                             final Color symbolColor) {
        final String symbol = getSymbol(imageModel.getYarnIndex(yarn));
        graphics.setColor(symbolColor);
        graphics.setFont(new Font(null, Font.PLAIN, FONT_SIZE));
        graphics.drawString(symbol, xPos + 2, yPos + BLOCK_SIZE - 2);
    }

    private String getSymbol(final int index) {
        // TODO make this a file later on
        final List<String> symbols =
                List.of("■",
                        "▲",
                        "◆",
                        "●",
                        "🞦",
                        "🞭",
                        "🞳",
                        "🟌",
                        "✿",
                        "❖",
                        "❤",
                        "◈",
                        "◉",
                        "◐",
                        "◑",
                        "⎈",
                        "⏣",
                        "0",
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6",
                        "7",
                        "8",
                        "9",
                        "A",
                        "B",
                        "C",
                        "G",
                        "H",
                        "K",
                        "M",
                        "X",
                        "Z");
        if (index > symbols.size()) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.NOT_ENOUGH_COLOR_SYMBOLS,
                    "Color index '" + index + "' is out of bounds of list of color symbols, which is: '"
                                                        + symbols.stream().collect(Collectors.joining("', '")) + "'!");
        }
        return symbols.get(index - 1); // index starts at 1
    }

}
