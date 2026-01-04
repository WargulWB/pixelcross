package com.github.wargulwb.pixelcross;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.cli.ArgumentHandler;
import com.github.wargulwb.pixelcross.cli.Arguments;
import com.github.wargulwb.pixelcross.cli.StdOutWriterImpl;
import com.github.wargulwb.pixelcross.cli.TestStdOutWriterImpl;
import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;
import com.github.wargulwb.pixelcross.model.ImageModel;
import com.github.wargulwb.pixelcross.model.ImageModelLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PixelCrossMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PixelCrossMain.class);

    private final boolean testMode;

    public PixelCrossMain(final boolean testMode) {
        this.testMode = testMode;
    }

    public static void testMain(final String[] args) {
        new PixelCrossMain(true).run(args);
    }

    public static void main(final String[] args) {
        new PixelCrossMain(false).run(args);
    }

    public void run(final String[] args) {

        final ArgumentHandler argumentHandler = new ArgumentHandler(testMode ? new TestStdOutWriterImpl() : new StdOutWriterImpl());
        if (argumentHandler.onlyShowHelp(args)) {
            argumentHandler.showHelp();
            return;
        }

        argumentHandler.init(args);

        final Arguments arguments = argumentHandler.getArguments();
        final Injector injector = initInjectionDependencies(arguments);
        final Path originalPixelArtImage = arguments.getInputImagePath();

        final ImageModel imageModel = injector.getInstance(ImageModelLoader.class).loadImageModel(originalPixelArtImage);

        final BufferedImage resultImage = injector.getInstance(CrossStitchPainter.class).paint(imageModel);
        writeImage(resultImage, originalPixelArtImage.getParent().resolve("@crosstitch.png"));
        final BufferedImage patternResultImage = injector.getInstance(CombinedPainter.class).paint(imageModel);
        writeImage(patternResultImage, originalPixelArtImage.getParent().resolve("@crosstitch_pattern.png"));
        LOGGER.debug("'pixelcross' terminating normally.");
    }

    private Injector initInjectionDependencies(final Arguments arguments) {
        final Injector injector = Guice.createInjector(new DependenciesModule(arguments));
        injector.getInstance(PixelCrossConfig.class).init();
        return injector;
    }

    private static void writeImage(final BufferedImage resultImage, final Path resultImagePath) {
        try {
            ImageIO.write(resultImage, "PNG", resultImagePath.toFile());
        } catch (final IOException exc) {
            throw new RuntimeErrorCodeException(ErrorCode.FAILED_TO_WRITE_IMAGE, "Failed to write image to '" + resultImagePath + "'!");
        }
    }

}
