package com.clone.workflow.service;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.repository.ProductDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clone.workflow.temporal.ShippingWorkFlow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ShippingService {

    @Autowired
    WorkflowServiceStubs workflowServiceStubs;

    @Autowired
    WorkflowClient workflowClient;

    @Autowired
    ProductDetailRepository productDetailRepository;

	/**
	 * This method initiates the workflow execution and saves the data in mongodb
	 * @param requestInfo
	 * @return Mono<ProductDetails>
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
    public Mono<ProductDetails> bookProductSendData(Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
		log.info("Inside bookProductSendData() method requestInfo : {}",requestInfo);
        ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());
        CompletableFuture<ProductDetails> productDetail = WorkflowClient.execute(workflow::startWorkflow, requestInfo);
		log.info("Saving ProductDetails database : {}",productDetail);
		Mono<ProductDetails> productDetailsMono = productDetailRepository.save(productDetail.get()).log();
        return productDetailsMono;
    }

	/**
	 * This method fetches data from mongoDb respository based on productId
	 * @param productId
	 * @return Mono<ProductDetails>
	 */
    public Mono<ProductDetails> getProduct(String productId) {
        log.info("Inside getProduct() method for productId : {}",productId);
        Mono<ProductDetails> productDetails = productDetailRepository.findById(productId);
		log.info("ProductDetails coming from database : {}",productDetails);
        return productDetails;
    }

	/**
	 * This method initiates the workflow execution and saves the data in mongodb
	 * @param requestInfo
	 * @return String
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
    public String bookProductSendString(Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
		log.info("Inside bookProductSendString() method for requestInfo : {}",requestInfo);
		ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());
        CompletableFuture<ProductDetails> productDetails = WorkflowClient.execute(workflow::startWorkflow, requestInfo);
		ProductDetails product = productDetails.get();
		log.info("Saving ProductDetails database : {}",product);
		Mono<ProductDetails> productDetailsMono = productDetailRepository.save(product);
        return "Booking done";
    }


	/**
	 * This method sets TaskQueue name, workflowId and returns ShippingWorkFlow as response
	 * @param id
	 * @return
	 */
    public ShippingWorkFlow createWorkFlowConnection(String id) {
		log.info("Inside createWorkFlowConnection() method id : {}",id);
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(ShippingWorkFlow.QUEUE_NAME)
                .setWorkflowId("Order_" + id).build();
        return workflowClient.newWorkflowStub(ShippingWorkFlow.class, options);
    }

}
