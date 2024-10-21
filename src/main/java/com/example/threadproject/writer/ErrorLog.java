package com.example.threadproject.writer;

import java.util.Date;

public class ErrorLog {
    private String fileName;
    private int recordNumber;
    private String errorCode;
    private Date errorDate;

    public ErrorLog(String fileName, int recordNumber, String errorCode) {
        this.fileName = fileName;
        this.recordNumber = recordNumber;
        this.errorCode = errorCode;
        this.errorDate = new Date();
    }

    // Getters and setters for all fields
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }
}
