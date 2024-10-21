package com.example.threadproject.batch;

import com.example.threadproject.enums.AccountType;
import com.example.threadproject.listener.JobCompletionNotificationListener;
import com.example.threadproject.model.Account;
import com.example.threadproject.model.Customer;
import com.example.threadproject.processor.AccountItemProcessor;
import com.example.threadproject.processor.CustomerItemProcessor;
import com.example.threadproject.repository.AccountRepository;
import com.example.threadproject.repository.CustomerRepository;
import com.example.threadproject.service.EncryptionService;
import com.example.threadproject.writer.AccountWriter;
import com.example.threadproject.writer.CustomerWriter;
import com.example.threadproject.writer.ErrorWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    // TaskExecutor for parallel processing
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5); // Adjust based on your system
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setThreadNamePrefix("Batch-Thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    // Account Item Reader
    @Bean
    @StepScope
    public FlatFileItemReader<Account> accountItemReader() {
        return new FlatFileItemReaderBuilder<Account>()
                .name("accountItemReader")
                .resource(new ClassPathResource("accounts.csv"))
                .delimited()
                .names(new String[]{"recordNumber", "accountNumber", "accountType", "accountCustomerId", "accountLimit", "accountOpenDate", "accountBalance"})
                .fieldSetMapper(fieldSet -> {
                    BeanWrapperFieldSetMapper<Account> mapper = new BeanWrapperFieldSetMapper<>();
                    mapper.setTargetType(Account.class);
                    Account account = mapper.mapFieldSet(fieldSet);

                    // Custom logic for setting AccountType
                    account.setAccountType(AccountType.valueOf(fieldSet.readString("accountType")));

                    return account;
                })
                .build();
    }

    // Customer Item Reader
    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(new ClassPathResource("customers.csv"))
                .delimited()
                .names(new String[]{"recordNumber", "customerID", "name", "surname", "address", "ZipCode", "nationalID", "birthDate"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Customer.class);
                }})
                .build();
    }

    // Account Step
    @Bean
    public Step accountStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                            AccountItemProcessor accountItemProcessor, AccountWriter accountWriter) {
        return new StepBuilder("accountStep", jobRepository)
                .<Account, Account>chunk(5, transactionManager)
                .reader(accountItemReader())
                .processor(accountItemProcessor)
                .writer(accountWriter)
//                .faultTolerant()
//                .skip(Exception.class)
//                .skipLimit(5)
//                .taskExecutor(taskExecutor()) // Enables parallel processing
//                .throttleLimit(5) // Add throttling if needed
                .allowStartIfComplete(true)  // Allow the job to start again even if it's complete
                .build();
    }

    // Customer Step
    @Bean
    public Step customerStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                             CustomerItemProcessor customerItemProcessor, CustomerWriter customerWriter) {
        return new StepBuilder("customerStep", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(customerItemReader())
                .processor(customerItemProcessor)
                .writer(customerWriter)
                .build();
    }

    // Account Writer Bean
    @Bean
    public AccountWriter accountWriter(AccountRepository accountRepository, ErrorWriter<Account> errorWriter, EncryptionService encryptionService) {
        return new AccountWriter(accountRepository, errorWriter, encryptionService);
    }

    // Customer Writer Bean
    @Bean
    public CustomerWriter customerWriter(CustomerRepository customerRepository,
                                         ErrorWriter<Customer> errorWriter) {
        return new CustomerWriter(customerRepository, errorWriter);
    }

    // Account Job
    @Bean
    public Job accountJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step accountStep) {
        log.info("Creating Account Job...");
        return new JobBuilder("accountJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(accountStep)
                .build();
    }

    // Customer Job
    @Bean
    public Job customerJob(JobRepository jobRepository, Step customerStep) {
        return new JobBuilder("customerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(customerStep)
                .build();
    }

    // Listener Bean for handling job completion
    @Bean
    public JobCompletionNotificationListener jobCompletionNotificationListener(JobLauncher jobLauncher, Job customerJob) {
        return new JobCompletionNotificationListener(jobLauncher, customerJob);
    }
}
