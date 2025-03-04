package com.microservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
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

}
