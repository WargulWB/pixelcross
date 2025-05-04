package com.github.wargulwb.pixelcross.error;

import java.util.Optional;

public class RuntimeErrorCodeException extends RuntimeException {

    private static final long serialVersionUID = 202503031204L;

    private final ErrorCode errorCode;
    private final String description;

    public RuntimeErrorCodeException(final ErrorCode code, final Throwable cause) {
        this(code, null, cause);
    }

    public RuntimeErrorCodeException(final ErrorCode code, final String description) {
        this(code, description, null);
    }

    public RuntimeErrorCodeException(final ErrorCode code, final String description, final Throwable cause) {
        super(cause);
        errorCode = Optional.ofNullable(code).orElse(ErrorCode.UNKNOWN_ERROR);
        this.description = description;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return Optional.ofNullable(description).orElse("");
    }

    @Override
    public String getMessage() {
        return new StringBuilder()
                .append("(")
                .append(errorCode.getCode())
                .append(",'")
                .append(errorCode.getText())
                .append("') ")
                .append(getDescription())
                .toString();
    }

}
