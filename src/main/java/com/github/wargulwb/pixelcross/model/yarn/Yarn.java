package com.github.wargulwb.pixelcross.model.yarn;

import java.awt.Color;
import java.util.Objects;

import com.github.wargulwb.pixelcross.utils.ColorHolder;

public class Yarn implements ColorHolder {

    private final Color color;
    private final String id;
    private final String name;
    private YarnSortiment yarnSortiment;

    private Yarn(final Builder builder) {
        color = Objects.requireNonNull(builder.bColor, getClass().getSimpleName() + ".color cannot be null!");
        id = Objects.requireNonNull(builder.bId, getClass().getSimpleName() + ".id cannot be null!");
        name = Objects.requireNonNull(builder.bName, getClass().getSimpleName() + ".name cannot be null!");
    }

    @Override
    public Color getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public YarnSortiment getYarnSortiment() {
        return yarnSortiment;
    }

    Yarn setYarnSortiment(final YarnSortiment yarnSortiment) {
        this.yarnSortiment = yarnSortiment;
        return this;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("[")
                .append("color=")
                .append(color)
                .append(", id=")
                .append(id)
                .append(", name=")
                .append(name)
                .append("]")
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Color bColor;
        private String bId;
        private String bName;

        private Builder() {
            // hide constructor
        }

        public Builder withColor(final Color value) {
            bColor = value;
            return this;
        }

        public Builder withId(final String value) {
            bId = value;
            return this;
        }

        public Builder withName(final String value) {
            bName = value;
            return this;
        }

        public Yarn build() {
            return new Yarn(this);
        }

    }
}
