package com.github.wargulwb.pixelcross.integrationtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.PixelCrossMain;

class PixelcrossITTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PixelcrossITTest.class);

    @TempDir
    Path tempDir;

    @Test
    void IT01Squared() throws IOException {
        test("IT01_SQUARED", "test_squared.png");
    }

    @Test
    void IT02Horizontal() throws IOException {
        test("IT02_HORIZONTAL", "test_horizontal.png");
    }

    @Test
    void IT03Vertical() throws IOException {
        test("IT03_VERTICAL", "test_vertical.png");
    }

    private void test(final String testDirectoryName, final String inputImageName) throws IOException {
        final Path testDir = Paths.get("src/test/integrationtest/").resolve(testDirectoryName);
        final Path testInputDir = testDir.resolve("input");
        final Path testOutputDir = testDir.resolve("output");

        final Path tmpImagePath = tempDir.resolve(inputImageName);
        Files.copy(testInputDir.resolve(inputImageName), tmpImagePath);

        LOGGER.info("Running application for IT '{}'!", testDirectoryName);
        PixelCrossMain.main(asArgs(tmpImagePath.toString()));
        LOGGER.info("Done running application for IT '{}'!", testDirectoryName);

        LOGGER.info("Veryfing outputs.");
        new ImageComparator().compare(tempDir.resolve("@crosstitch.png"), testOutputDir.resolve("@crosstitch.png"));
        new ImageComparator().compare(tempDir.resolve("@crosstitch_pattern.png"), testOutputDir.resolve("@crosstitch_pattern.png"));
    }

    private String[] asArgs(final String... args) {
        return args;
    }

}
