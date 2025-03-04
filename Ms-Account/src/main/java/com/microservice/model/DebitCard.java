package com.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "debit_cards")
public class DebitCard {

    @Id
    private String id;
    private String cardNumber;
    private String customerId;
    private List<String> accountIds;
    private String primaryAccountId;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private Boolean active;
}

