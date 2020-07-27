package com.riguz.opencv_helper.handler;

public class ExecuteException extends Exception {
    private final String errorCode;
    private final String description;

    public ExecuteException(String errorCode, String description, Exception cause) {
        super(cause);
        this.errorCode = errorCode;
        this.description = description;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}
