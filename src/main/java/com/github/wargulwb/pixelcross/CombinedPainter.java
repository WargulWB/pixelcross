package com.github.wargulwb.pixelcross;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import com.github.wargulwb.pixelcross.model.ImageModel;

import jakarta.inject.Inject;

public class CombinedPainter implements Painter {

    private static final double CM_TO_INCH = 1 / 2.54D;
    private static final double DPI_300 = 300D;

    /**
     * DIN A4 width := 21cm
     * Typically printers have: 300 DPI
     */
    private static final double DIN_A4_WIDTH = 21D * CM_TO_INCH * DPI_300;

    /**
     * DIN A4 height := 27.9cm
     * Typically printers have: 300 DPI
     */
    private static final double DIN_A4_HEIGHT = 29.7D * CM_TO_INCH * DPI_300;

    private final PatternPainter patternPainter;
    private final YarnLegendPainter yarnLegendPainter;
    private final CTMathLegendPainter ctMathLegendPainter;

    @Inject
    public CombinedPainter(final PatternPainter patternPainter,
            final YarnLegendPainter yarnLegendPainter,
            final CTMathLegendPainter ctMathLegendPainter) {
        this.patternPainter = Objects.requireNonNull(patternPainter, getClass().getSimpleName() + ".patternPainter cannot be null!");
        this.yarnLegendPainter =
                Objects.requireNonNull(yarnLegendPainter, getClass().getSimpleName() + ".yarnLegendPainter cannot be null!");
        this.ctMathLegendPainter =
                Objects.requireNonNull(ctMathLegendPainter, getClass().getSimpleName() + ".ctMathLegendPainter cannot be null!");
    }

    @Override
    public BufferedImage paint(final ImageModel imageModel) {
        final BufferedImage pattern = patternPainter.paint(imageModel);
        final BufferedImage yarnLegend = yarnLegendPainter.paint(imageModel);
        final BufferedImage ctCalculations = ctMathLegendPainter.paint(imageModel);

        final PositionModel bestPositionModel = determineBestPositionModel(pattern, yarnLegend, ctCalculations);
        final BufferedImage image = new BufferedImage(bestPositionModel.width(), bestPositionModel.height(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(AbstractPainter.BACKGROUND);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.drawImage(pattern, null, 0, 0);

        graphics.drawImage(yarnLegend, null, bestPositionModel.offsetLegend.x(), bestPositionModel.offsetLegend.y());
        graphics.drawImage(ctCalculations, null, bestPositionModel.offsetCalculations.x(), bestPositionModel.offsetCalculations.y());

        return image;
    }

    private PositionModel determineBestPositionModel(final BufferedImage pattern,
                                                     final BufferedImage yarnLegend,
                                                     final BufferedImage ctCalculations) {
        return List.of(
                determinePositionModelBelowEachOther(pattern, yarnLegend, ctCalculations),
                determinePositionModelBelowNextToEachOther(pattern, yarnLegend, ctCalculations),
                determinePositionModelOnTheRight(pattern, yarnLegend, ctCalculations))
                .stream()
                .sorted((pm1, pm2) -> {
                    final boolean pm1FitsDINA4 = pm1.fitsDINA4();
                    final boolean pm2FitsDINA4 = pm2.fitsDINA4();

                    // sort those which fit to the front
                    final int comp = Boolean.compare(pm2FitsDINA4, pm1FitsDINA4);
                    if (comp != 0) {
                        return comp;
                    }

                    final double area1 = pm1.width() * pm1.height();
                    final double area2 = pm2.width() * pm2.height();
                    return Double.compare(area1, area2); // smaller area has prio
                })
                .findFirst()
                .get();
    }

    private PositionModel determinePositionModelBelowEachOther(final BufferedImage pattern,
                                                               final BufferedImage yarnLegend,
                                                               final BufferedImage ctCalculations) {
        // PATTERN
        // ------------
        // YARN LEGEND
        // ------------
        // CALCULATIONS
        final int width = Math.max(Math.max(pattern.getWidth(), yarnLegend.getWidth()), ctCalculations.getWidth());
        final int height = pattern.getHeight() + yarnLegend.getHeight() + ctCalculations.getHeight();

        return new PositionModel(
                Placement.LEGEND_BOLOW_UNDER_EACH_OTHER,
                width,
                height,
                new Offset(0, pattern.getHeight()),
                new Offset(0, pattern.getHeight() + yarnLegend.getHeight()));
    }

    private PositionModel determinePositionModelBelowNextToEachOther(final BufferedImage pattern,
                                                                     final BufferedImage yarnLegend,
                                                                     final BufferedImage ctCalculations) {
        // PATTERN
        // ------------|-------------
        // YARN LEGEND | CALCULATIONS
        final int width = Math.max(pattern.getWidth(), yarnLegend.getWidth() + ctCalculations.getWidth());
        final int height = pattern.getHeight() + Math.max(yarnLegend.getHeight(), ctCalculations.getHeight());
        return new PositionModel(
                Placement.LEGEND_BOLOW_NEXT_TO_EACH_OTHER,
                width,
                height,
                new Offset(0, pattern.getHeight()),
                new Offset(yarnLegend.getWidth(), pattern.getHeight()));
    }

    private PositionModel determinePositionModelOnTheRight(final BufferedImage pattern,
                                                           final BufferedImage yarnLegend,
                                                           final BufferedImage ctCalculations) {
        // PATTERN ... | YARN LEGEND
        // ------------|-------------
        // ........... | CALCULATIONS
        final int width = pattern.getWidth() + Math.max(yarnLegend.getWidth(), ctCalculations.getWidth());
        final int height = Math.max(pattern.getHeight(), yarnLegend.getHeight() + ctCalculations.getHeight());
        return new PositionModel(
                Placement.LEGEND_ON_THE_RIGHT,
                width,
                height,
                new Offset(pattern.getWidth(), 0),
                new Offset(pattern.getWidth(), yarnLegend.getHeight()));
    }

    private record PositionModel(Placement placement, int width, int height, Offset offsetLegend, Offset offsetCalculations) {

        boolean fitsDINA4() {
            return fitsDINA4Vertically() || fitsDINA4Horizontally();
        }

        private boolean fitsDINA4Horizontally() {
            return height <= DIN_A4_WIDTH && width <= DIN_A4_HEIGHT;
        }

        private boolean fitsDINA4Vertically() {
            return width <= DIN_A4_WIDTH && height <= DIN_A4_HEIGHT;
        }

    }

    private record Offset(int x, int y) {
    }

    // this mainly serves to make debugging easier due to the variants being easily identifiable
    private enum Placement {
        LEGEND_BOLOW_UNDER_EACH_OTHER,
        LEGEND_BOLOW_NEXT_TO_EACH_OTHER,
        LEGEND_ON_THE_RIGHT;
    }

}
