package com.github.wargulwb.pixelcross;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;

class ArgumentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArgumentHandler.class);

    private static int EXPECTED_ARG_COUNT = 1;

    private final String[] args;
    private Path inputImage;

    ArgumentHandler(final String[] args) {
        this.args = args;
    }

    void init() {
        LOGGER.debug("Was called with arguments: '{}'", Arrays.stream(args).collect(Collectors.joining("', '")));
        if (args.length != EXPECTED_ARG_COUNT) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.ILLEGAL_ARGUMENT_COUNT,
                    "Number of arguments is " + args.length + " but expected is only 1 argument!");
        }
        final Path inputFileArg = Paths.get(args[0]);
        if (!Files.isRegularFile(inputFileArg)) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.INPUT_IMAGE_NO_FILE,
                    "Input image path given as first argument '" + args[0] + "' does not point to an existing file!");
        }
        inputImage = inputFileArg;
    }

    public Path getInputImageArgument() {
        return inputImage;
    }

}
