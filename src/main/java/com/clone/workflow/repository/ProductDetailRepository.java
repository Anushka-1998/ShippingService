package com.clone.workflow.repository;


import com.clone.workflow.domain.ProductDetails;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDetailRepository extends ReactiveMongoRepository<ProductDetails, String> {

}
