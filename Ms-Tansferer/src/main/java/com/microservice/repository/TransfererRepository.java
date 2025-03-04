package com.microservice.repository;

import com.microservice.model.Transferer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransfererRepository extends ReactiveCrudRepository<Transferer, String> {
}
