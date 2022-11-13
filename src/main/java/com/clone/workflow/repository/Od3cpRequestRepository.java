package com.clone.workflow.repository;

import com.clone.workflow.domain.Od3cpRequestInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface Od3cpRequestRepository extends ReactiveMongoRepository<Od3cpRequestInfo,String> {
}
