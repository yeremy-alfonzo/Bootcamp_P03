package com.microservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movements")
public class Movement {
    @Id
    private String id;
    private String idProduct;
    private String type;
    private double amount;
    private String description;
    @JsonFormat(pattern="dd-MM-yyyy", timezone="GMT-05:00")
    private Date date = new Date();
}
