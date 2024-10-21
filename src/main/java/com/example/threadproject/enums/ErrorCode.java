package com.example.threadproject.enums;

public enum ErrorCode {
    INVALID_ACCOUNT_TYPE("invalid_account_type"),
    BALANCE_LIMIT_EXCEEDED("balance_limit_exceeded"),
    INVALID_CUSTOMER_BIRTH_DATE("invalid_customer_birth_date"),
    INVALID_ACCOUNT_NUMBER("invalid_account_number"),
    INVALID_NATIONAL_ID("invalid_national_id"),
    MISSING_REQUIRED_FIELDS("missing_required_fields"),
    NULL_FIELD("field_is_null"), // Add this line
    INVALID_CUSTOMER("customer_not_found"),
    INVALID_CUSTOMER_NATIONAL_ID("invalid_customer_national_id"),
    INVALID_ACCOUNT_CUSTOMER_ID("invalid_account_customer_id"),
    INVALID_ACCOUNT_BALANCE("invalid_account_balance"); // Add this line
    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
