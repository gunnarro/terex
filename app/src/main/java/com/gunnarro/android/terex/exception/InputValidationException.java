package com.gunnarro.android.terex.exception;

public class InputValidationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE_FORMAT = "%s";
    private final String errorCode;

    public InputValidationException(String msg, String errorCode, Throwable throwable) {
        super(String.format(String.format(DEFAULT_MESSAGE_FORMAT, msg), errorCode), throwable);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
