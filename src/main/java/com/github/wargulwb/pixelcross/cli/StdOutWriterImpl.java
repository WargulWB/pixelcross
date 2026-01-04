package com.github.wargulwb.pixelcross.cli;

public class StdOutWriterImpl implements StdOutWriter {

    @Override
    public void writeLine(final String line) {
        System.out.println(line);
    }

}
