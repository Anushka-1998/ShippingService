package com.clone.workflow.repository;


import com.clone.workflow.domain.ProductDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

@Cacheable
public interface ProductDetailRepository extends ReactiveMongoRepository<ProductDetails, String> {

}
