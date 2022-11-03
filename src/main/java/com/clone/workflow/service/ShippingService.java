package com.clone.workflow.service;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.repository.ProductDetailRepository;
import io.temporal.client.WorkflowException;
import io.temporal.common.RetryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clone.workflow.temporal.ShippingWorkFlow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
		long start = System.currentTimeMillis();
        ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());

		ProductDetails productDetail = null;
		try {
			 //productDetail = WorkflowClient.execute(workflow::startWorkflow, requestInfo);
			productDetail = workflow.startWorkflow(requestInfo);
			log.info("Saving ProductDetails database : {}",productDetail);

		}
		catch (WorkflowException e) {
			log.error("***** Exception: " + e.getMessage());
			log.error("***** Cause: " + e.getCause().getClass().getName());
			log.error("***** Cause message: " + e.getCause().getMessage());
			long end = System.currentTimeMillis()-start;
			log.error("time taken to execute " + end);
		}

		if(!ObjectUtils.isEmpty(productDetail)){
			return productDetailRepository.save(productDetail).log();
		}

		return null;
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
		ShippingWorkFlow shippingWorkFlow = null;
		log.info("Inside createWorkFlowConnection() method id : {}",id);
        WorkflowOptions options = WorkflowOptions.newBuilder()
				.setWorkflowRunTimeout(Duration.ofSeconds(50))
				.setTaskQueue(ShippingWorkFlow.QUEUE_NAME)
                .setWorkflowId("Order_" + id)
				.setRetryOptions(RetryOptions.newBuilder()
						.setMaximumAttempts(2).build())
				//.setDoNotRetry(NullPointerException.class.getName()).build())
				.build();
		return workflowClient.newWorkflowStub(ShippingWorkFlow.class, options);
    }

}
