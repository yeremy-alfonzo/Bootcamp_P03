package com.microservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class Transferer {

    @Id
    private String id;
    private Double amount;
    private String typeTransfer;
    private Long idAccountOrigin;
    private Long idAccountDestination;
    @JsonFormat(pattern="dd-MM-yyyy", timezone="GMT-05:00")
    private Date date = new Date();
    private String description;

}
