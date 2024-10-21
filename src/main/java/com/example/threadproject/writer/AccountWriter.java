package com.example.threadproject.writer;

import com.example.threadproject.model.Account;
import com.example.threadproject.repository.AccountRepository;
import com.example.threadproject.service.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AccountWriter implements ItemWriter<Account> {
    private final AccountRepository accountRepository;
    private final ErrorWriter<Account> errorWriter;
    private final EncryptionService encryptionService;

    public AccountWriter(AccountRepository accountRepository, ErrorWriter<Account> errorWriter, EncryptionService encryptionService) {
        this.accountRepository = accountRepository;
        this.errorWriter = errorWriter;
        this.encryptionService = encryptionService;
    }

    @Override
    public void write(Chunk<? extends Account> chunk) throws Exception {
        log.info("Writing {} accounts to the database.", chunk.getItems().size());

        // Save valid accounts to the database
        accountRepository.saveAll(chunk.getItems());

        // Write all errors to the JSON file
        errorWriter.saveErrors();

        // Clear the errors after saving to avoid re-writing in the next chunk
        errorWriter.clearErrors();

        // Select customers with balance > 1000 and export them to JSON and XML
        List<Account> highBalanceAccounts = chunk.getItems().stream()
                .filter(account -> {
                    try {
                        // Decrypt the account balance
                        String decryptedBalance = encryptionService.decrypt(account.getAccountBalance());

                        // Convert to BigDecimal and compare
                        return new BigDecimal(decryptedBalance).compareTo(BigDecimal.valueOf(1000)) > 0;
                    } catch (Exception e) {
                        log.error("Error decrypting account balance for account {}: {}", account.getId(), e.getMessage());
                        return false; // Skip this account if decryption or conversion fails
                    }
                })
                .collect(Collectors.toList());

        if (!highBalanceAccounts.isEmpty()) {
            log.info("Accounts with balance > 1000 found: {}", highBalanceAccounts.size());
            writeToJSON(highBalanceAccounts);
            writeToXML(highBalanceAccounts);
        } else {
            log.info("No accounts with balance > 1000 found.");
        }
    }

    private void writeToJSON(List<Account> accounts) throws Exception {
        log.info("Writing {} accounts with balance > 1000 to JSON file.", accounts.size());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("high-balance-accounts.json"), accounts);
        log.info("JSON file created successfully: high-balance-accounts.json");
    }

    private void writeToXML(List<Account> accounts) throws Exception {
        log.info("Writing {} accounts with balance > 1000 to XML file.", accounts.size());
        JAXBContext jaxbContext = JAXBContext.newInstance(Account.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        for (Account account : accounts) {
            marshaller.marshal(account, new File("high-balance-account-" + account.getId() + ".xml"));
        }
        log.info("XML files created successfully for all high balance accounts.");
    }
}