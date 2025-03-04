package com.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Customer {

    @Id
    private String id;
    private String fullName;
    private Long numberIdentity;
    private String typeCustomer;
    private String typeProfile;
    private Integer phoneNumber;
    private String emailCustomer;
    private LocalDate dateDue;
    private BigDecimal OutstandingBalance;


}
