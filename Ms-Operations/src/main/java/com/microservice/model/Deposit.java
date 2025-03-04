package com.microservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@Document(collection = "deposit")
public class Deposit {

    @Id
    private String id;
    private Float amount;
    private String idAccount;
    @JsonFormat(pattern="dd-MM-yyyy" , timezone="GMT-05:00")
    private Date dateStart;

}
