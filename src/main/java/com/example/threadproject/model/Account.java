package com.example.threadproject.model;

import com.example.threadproject.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;
@XmlRootElement
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(name= "RECORD_NUMBER", unique = true, nullable = false)
    private Integer recordNumber;

    @NotNull
    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private String accountNumber; // This stores the AES encrypted value

    @NotNull(message = "Account type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE", nullable = false)
    private AccountType accountType;

    @NotNull
    @Column(name = "accountCustomerId", nullable = false)
    private Long accountCustomerId;

    @NotNull(message = "Account limit must not be null")
    @Column(name = "ACCOUNT_LIMIT", nullable = false)
    private Double accountLimit;

    @NotNull(message = "Account open date must not be null")
    @Temporal(TemporalType.DATE)
    @Column(name = "ACCOUNT_OPEN_DATE", nullable = false)
    private Date accountOpenDate;

    @NotNull
    @Column(name = "ACCOUNT_BALANCE", nullable = false)
    private String accountBalance; //Encrypted

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getAccountCustomerId() {
        return accountCustomerId;
    }

    public void setAccountCustomerId(Long customer) {
        this.accountCustomerId = customer;
    }

    public Double getAccountLimit() {
        return accountLimit;
    }

    public void setAccountLimit(Double accountLimit) {
        this.accountLimit = accountLimit;
    }

    public Date getAccountOpenDate() {
        return accountOpenDate;
    }

    public void setAccountOpenDate(Date accountOpenDate) {
        this.accountOpenDate = accountOpenDate;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", recordNumber=" + recordNumber +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", customer=" + accountCustomerId +
                ", accountLimit=" + accountLimit +
                ", accountOpenDate=" + accountOpenDate +
                ", accountBalance='" + accountBalance + '\'' +
                '}';
    }
}
