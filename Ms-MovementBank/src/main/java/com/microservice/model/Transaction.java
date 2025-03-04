package com.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String cardId;
    private String accountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
}

