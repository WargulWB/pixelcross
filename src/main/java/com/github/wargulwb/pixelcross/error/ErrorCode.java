package com.github.wargulwb.pixelcross.error;

public enum ErrorCode {

    UNKNOWN_ERROR(-1, "Unknown error"),
    FAILED_TO_LOAD_IMAGE(-2, "Failed to load image"),
    INVALID_IMAGE_FORMAT(-3, "Invalid image format"),
    FAILED_TO_WRITE_IMAGE(-4, "Failed to write image"),
    FAILED_TO_LOAD_CONFIG(-5, "Failed to load yarn data"),
    NOT_ENOUGH_COLOR_SYMBOLS(-6, "Not enough color symbols"),
    ILLEGAL_ARGUMENT_COUNT(-7, "Argument count not as expected"),
    INPUT_IMAGE_NO_FILE(-8, "Input image is no regular file");

    private final int code;
    private final String text;

    ErrorCode(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

}
