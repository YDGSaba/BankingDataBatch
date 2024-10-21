package com.example.threadproject.processor;

import com.example.threadproject.enums.ErrorCode;
public class ValidationException extends Exception {
    private final ErrorCode errorCode;

    public ValidationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        // Include error code name in the exception message
        return super.getMessage() + " (Error code: " + errorCode.getCode() + ")";
    }
}
