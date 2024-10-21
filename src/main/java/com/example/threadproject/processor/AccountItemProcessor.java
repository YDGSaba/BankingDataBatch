package com.example.threadproject.processor;

import com.example.threadproject.enums.ErrorCode;
import com.example.threadproject.model.Account;
import com.example.threadproject.service.EncryptionService;
import com.example.threadproject.writer.ErrorLog;
import com.example.threadproject.writer.ErrorWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountItemProcessor implements ItemProcessor<Account, Account> {

    @Autowired
    private ErrorWriter<Account> errorWriter;

    @Autowired
    private EncryptionService encryptionService;

    public Account validateAndProcessAccount(Account account) throws ValidationException {

        try {
            //Decrypt and validate account balance
            String decryptedBalance = encryptionService.decrypt(account.getAccountBalance());
            if (Double.parseDouble(decryptedBalance) > account.getAccountLimit()) {
                throw new ValidationException("Account Validation Failed", ErrorCode.BALANCE_LIMIT_EXCEEDED);
            }

            //Decrypt and validate account number (must be 22 digits)
            String decryptedAccountNumber = encryptionService.decrypt(account.getAccountNumber());
            if (decryptedAccountNumber.length() != 22 || !decryptedAccountNumber.matches("\\d{22}")) {
                throw new ValidationException("Account Validation Failed", ErrorCode.INVALID_ACCOUNT_NUMBER);
            }

        } catch (Exception e) {
            log.error("Decryption failed: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public Account process(Account account) {
        log.info("Processing account for {}", account);
        try {
            return validateAndProcessAccount(account);
        } catch (ValidationException e) {
            log.error("Validation failed for account: {}. Error: {}", account, e.getMessage());
            errorWriter.writeErrorLog(new ErrorLog("AccountsError.csv", account.getRecordNumber(), e.getErrorCode().name()));
            return null; // Skip this invalid record
        }
    }
}