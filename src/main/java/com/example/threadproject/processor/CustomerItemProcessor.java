package com.example.threadproject.processor;

import com.example.threadproject.model.Customer;
import com.example.threadproject.enums.ErrorCode;
import com.example.threadproject.service.EncryptionService;
import com.example.threadproject.writer.ErrorLog;
import com.example.threadproject.writer.ErrorWriter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {

    private final EncryptionService encryptionService;
    private final ErrorWriter<Customer> errorWriter;
    private final Validator validator;

    @Autowired
    public CustomerItemProcessor(EncryptionService encryptionService, ErrorWriter<Customer> errorWriter, Validator validator) {
        this.encryptionService = encryptionService;
        this.errorWriter = errorWriter;
        this.validator = validator;
    }
    public Customer validateAndProcessCustomer(Customer customer) throws ValidationException {
        log.debug("Customer before validation: {}", customer);

        log.info("Starting validation for customer with recordNumber: {}", customer.getRecordNumber());

        // Validate customer entity having no null field except the 'id'
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        log.debug("Customer details before validation: {}", customer);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> log.error("Validation error for recordNumber: {} - Field: {}, Message: {}",
                    customer.getRecordNumber(), violation.getPropertyPath(), violation.getMessage()));
            throw new ValidationException("Customer validation failed", ErrorCode.NULL_FIELD);
        }

        // Validate customer birth date > 1995
        Date birthDate = customer.getBirthDate();
        LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (birthLocalDate.getYear() <= 1995) {
            log.error("Customer birth date validation failed for recordNumber: {} - birth date is before 1995.", customer.getRecordNumber());
            throw new ValidationException("Invalid birth date", ErrorCode.INVALID_CUSTOMER_BIRTH_DATE);
        }

        try {
            // Decrypt and validate customer national ID
            customer.setNationalID(customer.getNationalID().trim());
            String decryptedNationalID = encryptionService.decrypt(customer.getNationalID());
            log.info("Decrypted National ID for customer with recordNumber: {} is: {}", customer.getRecordNumber(), decryptedNationalID);

            if (decryptedNationalID.length() != 10) {
                log.error("Invalid National ID for customer with recordNumber: {} - length is not 10.", customer.getRecordNumber());
                throw new ValidationException("Invalid National ID", ErrorCode.INVALID_NATIONAL_ID);
            }
            // Log completion of successful validation
            log.info("Customer with recordNumber: {} passed validation.", customer.getRecordNumber());

        } catch (Exception e) {
            log.error("Decryption failed for customer with recordNumber: {} - Error: {}", customer.getRecordNumber(), e.getMessage());
            throw new RuntimeException("Decryption failure: " + e.getMessage(), e);
        }

        return customer;
    }

    @Override
    public Customer process(Customer customer) throws ValidationException {
        log.info("Processing customer with recordNumber: {}", customer.getRecordNumber());

        try {
            // Perform validation and decryption
            return validateAndProcessCustomer(customer);

        } catch (ValidationException e) {
            log.error("Validation failed for customer with recordNumber: {}. Error: {}", customer.getRecordNumber(), e.getMessage());

            // Log the error and write to error log
            errorWriter.writeErrorLog(new ErrorLog("CustomersError.json", customer.getRecordNumber(), e.getErrorCode().toString()));

            return null;  // Skip this invalid record
        }
    }
}
