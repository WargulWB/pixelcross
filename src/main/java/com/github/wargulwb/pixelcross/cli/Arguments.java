package com.github.wargulwb.pixelcross.cli;

import java.awt.Color;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class Arguments {

    private final Path configPath;
    private final Path inputImagePath;
    private final Color treatAsTransparentColor;
    private final TransparentPixel treatAsTransparentPixel;

    private Arguments(final Builder builder) {
        configPath = builder.bConfigPath;
        inputImagePath = Objects.requireNonNull(builder.bInputImagePath, getClass().getSimpleName() + ".inputImagePath cannot be null!");
        treatAsTransparentColor = builder.bTreatAsTransparentColor;
        treatAsTransparentPixel = builder.bTreatAsTransparentPixel;
    }

    public Optional<Path> getConfigPath() {
        return Optional.ofNullable(configPath);
    }

    public Path getInputImagePath() {
        return inputImagePath;
    }

    public Optional<Color> getTreatAsTransparentColor() {
        return Optional.ofNullable(treatAsTransparentColor);
    }

    public Optional<TransparentPixel> getTreatAsTransparentPixel() {
        return Optional.ofNullable(treatAsTransparentPixel);
    }

    static Builder newBuilder() {
        return new Builder();
    }

    static class Builder {

        private Path bConfigPath;
        private Path bInputImagePath;
        private Color bTreatAsTransparentColor;
        private TransparentPixel bTreatAsTransparentPixel;

        Builder withConfigPath(final Path value) {
            bConfigPath = value;
            return this;
        }

        Builder withInputImagePath(final Path value) {
            bInputImagePath = value;
            return this;
        }

        Builder withTreatAsTransparentColor(final Color value) {
            bTreatAsTransparentColor = value;
            return this;
        }

        Builder withTreatAsTransparentPixel(final TransparentPixel value) {
            bTreatAsTransparentPixel = value;
            return this;
        }

        Arguments build() {
            return new Arguments(this);
        }

    }

}
