package com.gunnarro.android.terex.exception;

public class TerexApplicationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE_FORMAT = "Application error! Please Report error to app developer. Error=%s";
    private final String errorCode;

    public TerexApplicationException(String msg, String errorCode, Throwable throwable) {
        super(String.format(String.format(DEFAULT_MESSAGE_FORMAT, msg), errorCode), throwable);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
