package com.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movements_debit")
public class MovementDebit {

    @Id
    private String id;
    private String debitCardId;
    private String accountId;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime transactionDate;
    private String description;
    private String IdAccount;
    private LocalDateTime date;
}

