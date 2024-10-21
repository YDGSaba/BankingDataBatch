package com.example.threadproject.enums;
public enum AccountType {
    SAVINGS("savings"),
    RECURRING_DEPOSIT("recurring_deposit"),
    FIXED_DEPOSIT("fixed_deposit");

    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public static AccountType fromString(String code) throws IllegalArgumentException {
        for (AccountType type : AccountType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid account type: " + code);
    }
}
