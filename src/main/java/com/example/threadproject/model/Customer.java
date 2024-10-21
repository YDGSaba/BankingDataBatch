package com.example.threadproject.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import java.util.Date;
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name= "RECORD_NUMBER", unique = true, nullable = false)
    private Integer recordNumber;

    @NotNull(message = "Customer ID must not be null")
    @Column(name = "CUSTOMER_ID", unique = true, nullable = false)
    private Integer customerID;

    @NotEmpty(message = "Name cannot be empty")
    @Column(name = "CUSTOMER_NAME", nullable = false)
    private String name;

    @NotEmpty(message = "Surname cannot be empty")
    @Column(name = "CUSTOMER_SURNAME", nullable = false)
    private String surname;

    @NotNull
    @Column(name = "CUSTOMER_ADDRESS", nullable = false)
    private String address;

    @NotNull
    @Column(name = "CUSTOMER_ZIP_CODE", nullable = false)
    private String ZipCode;

    @NotEmpty(message = "National ID cannot be empty")
//    @Size(min = 10, max = 10, message = "National ID must be exactly 10 digits")
    @Column(name = "CUSTOMER_NATIONAL_ID", unique = true, nullable = false)
    private String nationalID;

    @NotNull(message = "Birth Date must not be null")
    @Temporal(TemporalType.DATE)
    @Column(name = "CUSTOMER_BIRTH_DATE", nullable = false)
    private Date birthDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", recordNumber=" + recordNumber +
                ", customerID=" + customerID +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", address='" + address + '\'' +
                ", ZipCode='" + ZipCode + '\'' +
                ", nationalID='" + nationalID + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
