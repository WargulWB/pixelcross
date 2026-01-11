package com.github.wargulwb.pixelcross.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.sax.XMLReaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wargulwb.pixelcross.cli.Arguments;
import com.github.wargulwb.pixelcross.error.ErrorCode;
import com.github.wargulwb.pixelcross.error.RuntimeErrorCodeException;
import com.github.wargulwb.pixelcross.model.yarn.Yarn;
import com.github.wargulwb.pixelcross.model.yarn.YarnSortiment;
import com.github.wargulwb.pixelcross.utils.JDOMUtils;

import jakarta.inject.Inject;

public class PixelCrossConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PixelCrossConfig.class);

    private static final Path CONFIG_PATH = Paths.get("config");
    private static final Path CONFIG_FILE = CONFIG_PATH.resolve("pixelcross.config.xml");

    private final Arguments arguments;
    private final JDOMUtils jdomUtils;
    private final List<YarnSortiment> sortiments = new ArrayList<>();
    private boolean outputGrayscale = false;

    @Inject
    PixelCrossConfig(final JDOMUtils jdomUtils, final Arguments arguments) {
        this.arguments = Objects.requireNonNull(arguments, getClass().getSimpleName() + ".arguments cannot be null!");
        this.jdomUtils = Objects.requireNonNull(jdomUtils, getClass().getSimpleName() + ".jdomUtils cannot be null!");
    }

    public List<YarnSortiment> getYarnSortiments() {
        return Collections.unmodifiableList(sortiments);
    }

    public boolean isOutputGrayscale() {
        return outputGrayscale;
    }

    public void init() {
        boolean usePackagedConfig = false;
        final Path configFilePath = arguments.getConfigPath().orElse(CONFIG_FILE);
        if (!Files.isRegularFile(configFilePath)) {
            LOGGER.warn(
                    "Failed to locate file '" + configFilePath + "' ('" + configFilePath.toAbsolutePath()
                        + "'). Going to use packaged default config.");
            usePackagedConfig = true;
        }

        if (usePackagedConfig) {
            try (final InputStream is = ClassLoader.getSystemResourceAsStream("pixelcross.config.xml")) {
                init(is);
            } catch (final IOException exc) {
                throw new RuntimeErrorCodeException(
                        ErrorCode.FAILED_TO_LOAD_CONFIG,
                        "Failed to get packaged fallback config 'src/main/resources/pixelcross.config.xml' as input stream!",
                        exc);
            }
        } else {
            init(configFilePath);
        }
    }

    private void init(final Path configFile) {
        try {
            final Document document = jdomUtils.readDocument(configFile, XMLReaders.NONVALIDATING);
            final Element configRoot = document.getRootElement();
            initGeneralConfig(configRoot);
            initYarnSortiments(configRoot);
        } catch (IOException | JDOMException exc) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.FAILED_TO_LOAD_CONFIG,
                    "Failed to load config from file '" + configFile + "'!",
                    exc);
        }
    }

    private void init(final InputStream configStream) {
        try {
            final Document document = jdomUtils.readDocument(configStream,
                    Paths.get("src/main/resources/pixelcross.config.xml").toUri().toString(),
                    XMLReaders.NONVALIDATING);
            final Element configRoot = document.getRootElement();
            initGeneralConfig(configRoot);
            initYarnSortiments(configRoot);
        } catch (IOException | JDOMException exc) {
            throw new RuntimeErrorCodeException(
                    ErrorCode.FAILED_TO_LOAD_CONFIG,
                    "Failed to load packaged fallback 'src/main/resources/pixelcross.config.xml'!",
                    exc);
        }
    }

    private void initGeneralConfig(final Element configRoot) {
        outputGrayscale = jdomUtils.getBooleanAttribute(configRoot, "output/grayscale", false);
    }

    private void initYarnSortiments(final Element configRoot) {
        final Element yarnDataNode = configRoot.getChild("yarndata");
        final List<YarnSortiment> yarnSortiments =
                yarnDataNode.getChildren("sortiment")
                        .stream()
                        .map(this::parseYarnSortiment)
                        .toList();
        sortiments.addAll(yarnSortiments);
    }

    private YarnSortiment parseYarnSortiment(final Element sortimentNode) {
        return YarnSortiment.newBuilder()
                .withName(jdomUtils.getStringAttribute(sortimentNode, "name", null))
                .withProducer(jdomUtils.getStringAttribute(sortimentNode, "producer", null))
                .withYarns(sortimentNode.getChildren("yarn")
                        .stream()
                        .map(this::parseYarn)
                        .toList())
                .build();
    }

    private Yarn parseYarn(final Element yarnNode) {
        return Yarn.newBuilder()
                .withName(jdomUtils.getStringAttribute(yarnNode, "name", null))
                .withId(jdomUtils.getStringAttribute(yarnNode, "id", null))
                .withColor(jdomUtils.getColorAttribute(yarnNode, "rgb", null))
                .build();
    }

}
