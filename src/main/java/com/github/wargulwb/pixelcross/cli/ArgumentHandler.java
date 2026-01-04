package com.github.wargulwb.pixelcross.cli;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;
import com.github.wargulwb.pixelcross.utils.ColorUtils;

/**
 * I could use PicoCLI but than I would have to integrate it with Guice, which might be the better solution in the long run, for now this
 * should suffice.
 */
public class ArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentHandler.class);

    //@formatter:off
    private static Option HELP = new Option(
            "h",
            "help",
            "Shows the CLI usage information. If specified all other arguments are ignored."
            );
    private static Option CONFIG_PATH = new Option(
            "c",
            "config",
            "configFilePath",
            "Per default the config file is loaded from 'config/pixelcross.config.xml'"
          + " (realtive to the directory from which you started the application)."
          + " If that file is not available a default config, packaged with the application is loaded."
          + " This option allows to specify a different path from which the config is loaded instead."
            );
    private static Option TREAT_COLOR_AS_TRANSPARENT = new Option(
            "t",
            "treat-color-trasparent",
            "color",
            "The color has to be given as rgb value '#RRGGBB'."
          + " Each pixel matching the color is treated as if it were transparent."
            );
    private static Option TREAT_PIXEL_AS_TRANSPARENT = new Option(
            "p",
            "treat-pixel-trasparent",
            "pixelPosition",
            "The pixel has to be given as coordinate '<horinzontal>,<vertical>' e.g. '0,0' or as 'upper-left'."
          + " Each pixel matching the color of the specified pixel is treated as if it were transparent."
            );
    //@formatter:on

    private final StdOutWriter stdOutWriter;

    private Arguments arguments; // this is unfortunately modifiable by design

    public ArgumentHandler(final StdOutWriter stdOutWriter) {
        this.stdOutWriter = Objects.requireNonNull(stdOutWriter, getClass().getSimpleName() + ".stdOutWriter cannot be null!");
    }

    public void init(final String[] args) {
        LOGGER.debug("Was called with arguments: '{}'", Arrays.stream(args).collect(Collectors.joining("', '")));
        if (onlyShowHelp(args)) {
            return;
        }

        validateNoInvalidOptions(args);

        arguments = Arguments.newBuilder()
                .withConfigPath(resolveConfigPathOption(args))
                .withInputImagePath(resolveInputImageArgument(args))
                .withTreatAsTransparentColor(resolveTreatAsTransparentColorArgument(args))
                .withTreatAsTransparentPixel(resolveTreatAsTransparentPixelArgument(args))
                .build();
    }

    private TransparentPixel resolveTreatAsTransparentPixelArgument(final String[] args) {
        final Optional<String> transparentColorValue = getValueForOption(TREAT_PIXEL_AS_TRANSPARENT, args);
        if (transparentColorValue.isEmpty()) {
            return null;
        }

        TransparentPixel.validate(transparentColorValue.get());

        return new TransparentPixel(transparentColorValue.get());
    }

    private Color resolveTreatAsTransparentColorArgument(final String[] args) {
        final Optional<String> transparentColorValue = getValueForOption(TREAT_COLOR_AS_TRANSPARENT, args);
        if (transparentColorValue.isEmpty()) {
            return null;
        }

        final Pattern rgbPattern = Pattern.compile("#[[0-9][A-F]]{6}");
        final Matcher matcher = rgbPattern.matcher(transparentColorValue.get());
        if (!matcher.matches()) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.INVALID_TRANSPARENT_COLOR,
                    "The value '"
                                                         + transparentColorValue.get()
                                                         + "# does not match the RGB color pattern '"
                                                         + rgbPattern.pattern()
                                                         + "' (e.g. '#0000FF').");
        }

        return new ColorUtils().colorFromRGBHexString(transparentColorValue.get());
    }

    private Path resolveConfigPathOption(final String[] args) {
        final Optional<String> configPathValue = getValueForOption(CONFIG_PATH, args);
        if (configPathValue.isEmpty()) {
            return null;
        }

        final Path configFilePath = Paths.get(configPathValue.get());
        if (!Files.isRegularFile(configFilePath)) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.CONFIG_NO_FILE,
                    "Config path '" + configFilePath + "' (absolute '" + configFilePath.toAbsolutePath()
                                              + "') does not point to an existing file!");
        }

        return configFilePath;
    }

    private void validateNoInvalidOptions(final String[] args) {
        final List<Option> options = List.of(HELP, CONFIG_PATH, TREAT_COLOR_AS_TRANSPARENT, TREAT_PIXEL_AS_TRANSPARENT);
        final Pattern optionPattern = Pattern.compile("--?.*");

        final List<String> invalidOptions = Arrays.stream(args)
                .filter(arg -> optionPattern.matcher(arg).matches())
                .filter(arg -> options.stream().noneMatch(opt -> opt.applies(arg)))
                .toList();
        if (!invalidOptions.isEmpty()) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.INVALID_OPTION,
                    "The following options are invalid: '" + invalidOptions.stream().collect(Collectors.joining("', '")) + "'.");
        }
    }

    private Path resolveInputImageArgument(final String[] args) {
        final Pattern nonOptionPattern = Pattern.compile("[^-].*");
        final List<String> matchingArguments = Arrays.stream(args)
                .filter(arg -> nonOptionPattern.matcher(arg).matches())
                .toList();

        if (matchingArguments.size() == 0) {
            throw new RuntimeErrorCodeException(ErrorCode.MISSING_FILE_PATH_ARGUMENT);
        }
        if (matchingArguments.size() > 1) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.TOO_MANY_NON_OPTION_ARGUMENTS,
                    "Found " + matchingArguments.size() + " non option arguments: '"
                                                             + matchingArguments.stream().collect(Collectors.joining("', '")) + "'");
        }
        final String argument = matchingArguments.get(0);
        final Path inputFileArg = Paths.get(argument);
        if (!Files.isRegularFile(inputFileArg)) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.CONFIG_NO_FILE,
                    "Input image path given as argument '" + argument + "' does not point to an existing file!");
        }
        return inputFileArg;
    }

    public boolean onlyShowHelp(final String[] args) {
        return args.length == 0 || Arrays.stream(args).anyMatch(arg -> HELP.getBooleanValue(arg));
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void showHelp() {
        final StringBuilder sb = new StringBuilder();
        sb.append("java -jar pixelcross-*-jar-with-dependencies.jar");
        sb.append(" ").append(HELP.getOptionString());
        sb.append(" ").append(CONFIG_PATH.getOptionString());
        sb.append(" ").append(TREAT_COLOR_AS_TRANSPARENT.getOptionString());
        sb.append(" ").append(TREAT_PIXEL_AS_TRANSPARENT.getOptionString());
        sb.append(" <pixelArtImagePath>");

        sb.append("\n")
                .append("Creates a cross stitching example image and a pattern image with paths relative to the path of the input pixel art image.\n")
                .append(HELP.getOptionDescription())
                .append(CONFIG_PATH.getOptionDescription())
                .append(TREAT_COLOR_AS_TRANSPARENT.getOptionDescription())
                .append(TREAT_PIXEL_AS_TRANSPARENT.getOptionDescription());
        stdOutWriter.writeLine(sb.toString());
    }

    private Optional<String> getValueForOption(final Option option, final String[] args) {
        return Arrays.stream(args)
                .map(arg -> option.getValue(arg).orElse(null))
                .filter(val -> val != null)
                .findFirst();
    }

}
