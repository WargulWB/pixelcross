package com.github.wargulwb.pixelcross.utils;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

import jakarta.inject.Inject;

public class JDOMUtils {

    private static final String NODE_PATH_SEPARATOR = "/";

    private final BooleanConverter booleanConverter;
    private final ColorConverter colorConverter;

    @Inject
    JDOMUtils(final BooleanConverter booleanConverter, final ColorConverter colorConverter) {
        this.booleanConverter = Objects.requireNonNull(booleanConverter, getClass().getSimpleName() + ".booleanConverter cannot be null!");
        this.colorConverter = Objects.requireNonNull(colorConverter, getClass().getSimpleName() + ".colorConverter cannot be null!");
    }

    public Document readDocument(final Path path, final XMLReaders reader) throws IOException, JDOMException {
        if (path == null) {
            throw new IllegalArgumentException("Document path must not be null!");
        }
        final SAXBuilder builder = new SAXBuilder(Optional.ofNullable(reader).orElse(XMLReaders.NONVALIDATING));
        try (InputStream is = Files.newInputStream(path)) {
            // https://bugs.openjdk.java.net/browse/JDK-8226399
            final Document result = builder.build(is);
            result.setBaseURI(path.toUri().toString());
            return result;
        }
    }

    /**
     * Returns the requested attribute value as string.
     *
     * @param node
     *            node to extract attribute value from
     * @param path
     *            path to the target attribute
     * @param defaultValue
     *            default returnvalue
     * @return the attribute's value if node is not null and attribute value is
     *         set, defaultvalue otherwise
     */
    public String getStringAttribute(final Element node, final String path, final String defaultValue) {
        return Optional
                .ofNullable(getAttribute(node, path))
                .map(Attribute::getValue)
                .orElse(defaultValue);
    }

    /**
     * Returns the requested attribute value as Boolean.
     *
     * @param node
     *            node to extract attribute value from
     * @param path
     *            name of the attribute to get value for
     * @param defaultValue
     *            default returnvalue
     * @return the attribute's value if node is not null and attribute value is
     *         set to a valid Boolean value, defaultvalue otherwise
     */
    public Boolean getBooleanAttribute(final Element node, final String path, final Boolean defaultValue) {
        return Optional
                .ofNullable(getAttribute(node, path))
                .map(Attribute::getValue)
                .map(booleanConverter::fromString)
                .orElse(defaultValue);
    }

    /**
     * Returns the requested attribute value as Boolean.
     *
     * @param node
     *            node to extract attribute value from
     * @param path
     *            name of the attribute to get value for
     * @param defaultValue
     *            default returnvalue
     * @return the attribute's value if node is not null and attribute value is
     *         set to a valid Boolean value, defaultvalue otherwise
     */
    public Color getColorAttribute(final Element node, final String path, final Color defaultValue) {
        return Optional
                .ofNullable(getAttribute(node, path))
                .map(Attribute::getValue)
                .map(colorConverter::fromString)
                .orElse(defaultValue);
    }

    /**
     * Returns the requested attribute.
     *
     * @param node
     *            node to extract attribute value from
     * @param path
     *            path to the target attribute
     * @return attribute if found, else null
     */
    public Attribute getAttribute(final Element node, final String path) {
        final String attributeName = getElementName(path);
        if (attributeName == null) {
            return null;
        }

        return locateNode(node, getElementPath(path))
                .map(n -> n.getAttribute(attributeName))
                .orElse(null);
    }

    private static String getElementName(final String path) {
        final int index = StringUtils.lastIndexOf(path, NODE_PATH_SEPARATOR);

        if (index >= 0) {
            return path.substring(index + 1);
        }

        return path;
    }

    private static String getElementPath(final String path) {
        final int index = StringUtils.lastIndexOf(path, NODE_PATH_SEPARATOR);

        if (index >= 0) {
            return path.substring(0, index);
        }

        return null;
    }

    /**
     * Returns the element text of the node specified by the given path and node.
     *
     * @param node
     *            {@link Element} object
     * @param path
     *            sub path from node or null to set text of node
     * @param defaultValue
     *            default value, if no text could be retrieved or is empty
     * @return element text as String or defaultValue
     */
    public String getText(final Element node, final String path, final String defaultValue) {
        return locateNode(node, path)
                .map(Element::getText)
                .filter(text -> !StringUtils.isEmpty(text))
                .orElse(defaultValue);
    }

    private Optional<Element> locateNode(final Element node, final String path) {
        if (StringUtils.isEmpty(path)) {
            return Optional.ofNullable(node);
        }

        return Optional.ofNullable(getChildNode(node, path));
    }

    /**
     * Returns the requested child node.
     *
     * @param node
     *            node to extract child node from
     * @param child
     *            name of the child node to return
     * @return the JDOM child node or null if child does not exist or given
     *         node is null.
     */
    public Element getChildNode(final Element node, final String child) {
        return traversePath(node, child, null);
    }

    private Element traversePath(final Element node, final String child, final BiFunction<String, Namespace, Element> function) {
        if (node == null || StringUtils.isEmpty(child) || child.endsWith(NODE_PATH_SEPARATOR) || child.startsWith(NODE_PATH_SEPARATOR)) {
            return null;
        }

        final Optional<BiFunction<String, Namespace, Element>> factory = Optional.ofNullable(function);

        Element curNode = node;
        final StringTokenizer tokenizer = new StringTokenizer(child, NODE_PATH_SEPARATOR);
        while (curNode != null && tokenizer.hasMoreElements()) {
            final String token = (String) tokenizer.nextElement();

            final Namespace namespace = curNode.getNamespace();
            Element nextNode = curNode.getChild(token, namespace);
            if (nextNode == null && factory.isPresent()) {
                nextNode = factory.map(f -> f.apply(token, namespace)).get();
                curNode.addContent(nextNode);
            }
            curNode = nextNode;
        }

        return curNode;
    }

    interface InterruptableWriter {
        void write() throws IOException;
    }

}
