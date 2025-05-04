package com.github.wargulwb.pixelcross.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.wargulwb.pixelcross.model.yarn.Yarn;

public class ImageModel {

    private final int width;
    private final int height;

    private final PixelCross[][] grid;
    private final List<PixelCrossColor> colors;
    private final Map<Yarn, YarnData> yarnIndexMap = new HashMap<>(); // unfortunately modifiable

    ImageModel(final int width, final int height, final PixelCross[][] grid, final List<PixelCrossColor> colors) {
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.colors = colors;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PixelCross getPixelCross(final int x, final int y) {
        if (x < 0 || x >= width) {
            throw new IllegalArgumentException("Given paramet 'x'=" + x + " is out of bounds [0, " + width + ")!");
        }
        if (y < 0 || y >= height) {
            throw new IllegalArgumentException("Given paramet 'y'=" + y + " is out of bounds [0, " + height + ")!");
        }
        return grid[x][y];
    }

    public List<PixelCrossColor> getColors() {
        return Collections.unmodifiableList(colors);
    }

    public void updateYarnIndexMap() {
        yarnIndexMap.clear();
        int index = 1;
        for (final Yarn distinctYarn : colors.stream()
                .filter(PixelCrossColor::isPixelOpaque) // only leave opaque colors
                .map(PixelCrossColor::getYarn)
                .distinct()
                .toList()) {
            yarnIndexMap.put(distinctYarn, new YarnData(index, countCrossesOfYarn(distinctYarn)));
            index++;
        }
    }

    private int countCrossesOfYarn(final Yarn yarn) {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].getPixelColor().getYarn() == yarn) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getYarnIndex(final Yarn yarn) {
        return Optional.ofNullable(yarnIndexMap.get(yarn))
                .map(y -> y.index())
                .orElse(-1);
    }

    public int getYarnCrossCount(final Yarn yarn) {
        return Optional.ofNullable(yarnIndexMap.get(yarn))
                .map(y -> y.crossCount())
                .orElse(0);
    }

    public int getEffectiveColorCount() {
        return yarnIndexMap.size();
    }

    public Collection<Yarn> getYarns() {
        return Collections.unmodifiableCollection(yarnIndexMap.keySet());
    }

    private record YarnData(int index, int crossCount) {
    }

}
