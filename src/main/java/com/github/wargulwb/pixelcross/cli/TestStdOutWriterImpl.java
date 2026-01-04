package com.github.wargulwb.pixelcross.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestStdOutWriterImpl implements StdOutWriter {

    private final static List<String> BUFFER = new ArrayList<>();

    @Override
    public void writeLine(final String line) {
        System.out.println(line);
        BUFFER.add(line);
    }

    public static List<String> getBuffer() {
        return Collections.unmodifiableList(BUFFER);
    }

    public static void clearBuffer() {
        BUFFER.clear();
    }

    public static String getBufferAsString() {
        final StringBuilder sb = new StringBuilder();

        for (final String line : BUFFER) {
            sb.append(line);
        }

        return sb.toString();
    }

}
