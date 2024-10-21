package com.example.threadproject.writer;

import com.example.threadproject.model.Customer;
import com.example.threadproject.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerWriter implements ItemWriter<Customer> {
    private final CustomerRepository customerRepository;
    private final ErrorWriter<Customer> errorWriter;

    public CustomerWriter(CustomerRepository customerRepository, ErrorWriter<Customer> errorWriter) {
        this.customerRepository = customerRepository;
        this.errorWriter = errorWriter;
    }

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        log.info("Writing {} customers to the database.", chunk.getItems().size());

        // Save valid customers to the database
        customerRepository.saveAll(chunk.getItems());

        // Save all errors to the JSON file
        errorWriter.saveErrors();
    }
}
