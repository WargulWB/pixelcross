package com.github.wargulwb.pixelcross.integrationtest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.PixelCrossMain;
import com.github.wargulwb.pixelcross.cli.TestStdOutWriterImpl;

class PixelcrossITTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PixelcrossITTest.class);

    private static final String EXPECTED_HELP_TEXT =
            """
                    java -jar pixelcross-*-jar-with-dependencies.jar [-h] [-c=<configFilePath>] [-t=<color>] [-p=<pixelPosition>] <pixelArtImagePath>
                    Creates a cross stitching example image and a pattern image with paths relative to the path of the input pixel art image.
                    -h, --help
                      Shows the CLI usage information. If specified all other arguments are ignored.
                    -c, --config=<configFilePath>
                      Per default the config file is loaded from 'config/pixelcross.config.xml' (relative to the directory from which you started the application). If that file is not available a default config, packaged with the application is loaded. This option allows to specify a different path from which the config is loaded instead.
                    -t, --treat-color-trasparent=<color>
                      The color has to be given as rgb value '#RRGGBB'. Each pixel matching the color is treated as if it were transparent.
                    -p, --treat-pixel-trasparent=<pixelPosition>
                      The pixel has to be given as coordinate '<horinzontal>,<vertical>' e.g. '0,0' or as 'top_left, top_right, bottom_left, bottom_right'. Each pixel matching the color of the specified pixel is treated as if it were transparent.
                                                    """;

    private static String ARG_PLACEHOLDER_TEST_CONFIG = "$TEST_CONFIG";

    @BeforeEach
    void beforeEach() {
        // clean up
        TestStdOutWriterImpl.clearBuffer();
    }

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

    @Test
    void IT04CLIHelpNoArgs() throws IOException {
        testConsole("IT04_CLI_Help_No_Args", EXPECTED_HELP_TEXT);
    }

    @Test
    void IT05CLIHelpWithArgs() throws IOException {
        // if help is specified, other options are irrelevant
        testConsole("IT05_CLI_Help_With_Args", EXPECTED_HELP_TEXT, "-invalid", "-h");
    }

    @Test
    void IT06CLITransparentColor() throws IOException {
        test("IT06_CLI_TRANSPARENT_COLOR", "test.png", "-t=#FF00FF");
    }

    @Test
    void IT07CLITransparentPixel() throws IOException {
        test("IT07_CLI_TRANSPARENT_PIXEL", "test.png", "-p=top_left");
    }

    @Test
    void IT08CLITransparentPixelCoordinate() throws IOException {
        test("IT08_CLI_TRANSPARENT_PIXEL_COORDINATE", "test.png", "-p=0,0");
    }

    @Test
    void IT09AlternativeConfig() throws IOException {
        test("IT09_ALTERNATIVE_CONFIG", "test.png", "-p=0,0", "-c=$TEST_CONFIG");
    }

    private void testConsole(final String testDirectoryName, final String expectedOutput, final String... args) {
        // run
        LOGGER.info("Running application for IT '{}'!", testDirectoryName);
        PixelCrossMain.testMain(args);
        LOGGER.info("Done running application for IT '{}'!", testDirectoryName);

        final String output = TestStdOutWriterImpl.getBufferAsString();
        assertThat(output, is(expectedOutput));
    }

    private void test(final String testDirectoryName, final String inputImageName, final String... args) throws IOException {
        final Path testDir = Paths.get("src/test/integrationtest/").resolve(testDirectoryName);
        final Path testInputDir = testDir.resolve("input");
        final Path testOutputDir = testDir.resolve("output");

        // prepare
        final Path tmpImagePath = tempDir.resolve(inputImageName);
        copy(testInputDir.resolve(inputImageName), tmpImagePath);
        final Path testConfigPath = testInputDir.resolve("pixelcross.config.xml");
        final Path tempConfigPath = tempDir.resolve("pixelcross.config.xml");
        if (Files.isRegularFile(testConfigPath)) {
            copy(testConfigPath, tempConfigPath);
        }

        // run
        LOGGER.info("Running application for IT '{}'!", testDirectoryName);
        PixelCrossMain.testMain(asArgs(tmpImagePath.toString(), adjustArgs(args, tempConfigPath)));
        LOGGER.info("Done running application for IT '{}'!", testDirectoryName);

        // validate
        LOGGER.info("Veryfing outputs.");
        new ImageComparator().compare(tempDir.resolve("@crosstitch.png"), testOutputDir.resolve("@crosstitch.png"));
        new ImageComparator().compare(tempDir.resolve("@crosstitch_pattern.png"), testOutputDir.resolve("@crosstitch_pattern.png"));

    }

    private String[] adjustArgs(final String[] args, final Path tempConfigPath) {
        final String[] adjustedArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            if (!args[i].contains(ARG_PLACEHOLDER_TEST_CONFIG)) {
                adjustedArgs[i] = args[i];
            } else {
                adjustedArgs[i] = args[i].replace(ARG_PLACEHOLDER_TEST_CONFIG, tempConfigPath.toString());
            }
        }
        return adjustedArgs;
    }

    private String[] asArgs(final String inputImageName, final String... args) {
        final String[] adjustedArgs = new String[args.length + 1];
        adjustedArgs[adjustedArgs.length - 1] = inputImageName;
        for (int i = 0; i < args.length; i++) {
            adjustedArgs[i] = args[i];
        }
        return adjustedArgs;
    }

    private static void copy(final Path src, final Path dest) throws IOException {
        LOGGER.info("Copying '" + src + "' to '" + dest + "!");
        Files.copy(src, dest);
    }

}
