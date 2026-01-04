package com.github.wargulwb.pixelcross.cli;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Option {

    private final String argumentName;
    private final String description;
    private final String longName;
    private final String shortName;
    private final boolean isBoolean;

    public Option(final String shortName,
            final String longName,
            final String argumentName,
            final String description) {
        this.argumentName = argumentName;
        this.description = Objects.requireNonNull(description, getClass().getSimpleName() + ".description cannot be null!");
        this.longName = Objects.requireNonNull(longName, getClass().getSimpleName() + ".longName cannot be null!");
        this.shortName = Objects.requireNonNull(shortName, getClass().getSimpleName() + ".shortName cannot be null!");
        isBoolean = argumentName == null;
    }

    public Option(final String shortName,
            final String longName,
            final String description) {
        this(shortName, longName, null, description);
    }

    public Optional<String> getValue(final String argument) {
        if (isBoolean) {
            return Optional.empty(); // not providing string value for boolean option
        }
        final Pattern pattern = Pattern.compile("(-" + shortName + "|--" + longName + ")=(?<value>.*)");
        final Matcher matcher = pattern.matcher(argument);
        if (matcher.matches()) {
            return Optional.of(matcher.group("value"));
        }
        return Optional.empty();
    }

    public boolean getBooleanValue(final String argument) {
        if (!isBoolean) {
            return false; // not providing boolean value for String option
        }

        return ("-" + shortName).equals(argument) || ("--" + longName).equals(argument);
    }

    public boolean applies(final String arg) {
        return isBoolean ? getBooleanValue(arg) : !getValue(arg).isEmpty();
    }

    public String getDescription() {
        return description;
    }

    public String getOptionString() {
        if (isBoolean) {
            return "[-" + shortName + "]";
        }
        return "[-" + shortName + "=<" + argumentName + ">]";
    }

    public String getOptionDescription() {
        final StringBuilder sb = new StringBuilder();

        sb.append("-")
                .append(shortName)
                .append(", ")
                .append("--")
                .append(longName);

        if (!isBoolean) {
            sb.append("=<")
                    .append(argumentName)
                    .append(">");
        }

        sb.append("\n  ")
                .append(description)
                .append("\n");

        return sb.toString();
    }

}
