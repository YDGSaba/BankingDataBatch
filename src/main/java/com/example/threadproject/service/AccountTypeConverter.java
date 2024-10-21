package com.example.threadproject.service;
// Adjust package name based on your project

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.example.threadproject.enums.AccountType;

@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, String> {

    @Override
    public String convertToDatabaseColumn(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return accountType.toString();  // Save as string in DB
    }

    @Override
    public AccountType convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return AccountType.fromString(dbData);  // Convert back to enum
    }
}
