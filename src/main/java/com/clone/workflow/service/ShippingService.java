package com.clone.workflow.service;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.repository.ProductDetailRepository;
import io.temporal.api.common.v1.WorkflowExecution;
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

	public ProductDetails placeOrder(Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
		ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());
		//WorkflowClient.start(workflow::startApprovalWorkflow);
	//	WorkflowExecution workflowExecution = WorkflowClient.start(workflow::startApprovalWorkflow,requestInfo);
		CompletableFuture<ProductDetails> productDetails = WorkflowClient.execute(workflow::startApprovalWorkflow,requestInfo);
		//ProductDetails productDetails = workflow.getProductUsingQuery();
		ProductDetails product = productDetails.get();
		Mono<ProductDetails> productDetailsMono = productDetailRepository.save(product);
		return product;
		//return workflow.startApprovalWorkflow(requestInfo);
	}

	private ProductDetails product;

	public ProductDetails getProduct(String productId) {
		log.info("Inside getProduct");
		productDetailRepository.findById(productId);
		Mono<ProductDetails> productDetails = productDetailRepository.findById(productId);
		productDetails.subscribe(prod->{
			this.product= prod;
		    log.info("prod : {}",prod);
		});
		System.out.println(product);
		return product;
	}

	public void makeOrderAccepted(String workflowId) {
		ShippingWorkFlow workflow = workflowClient.newWorkflowStub(ShippingWorkFlow.class, "Order_" + workflowId);
		workflow.signalOrderAccepted();
	}

	public void makeOrderPickedUp(String workflowId) {
		ShippingWorkFlow workflow = workflowClient.newWorkflowStub(ShippingWorkFlow.class, "Order_" + workflowId);
		workflow.signalOrderPickedUp();
	}

	public void makeOrderDelivered(String workflowId) {
		ShippingWorkFlow workflow = workflowClient.newWorkflowStub(ShippingWorkFlow.class, "Order_" + workflowId);
		workflow.signalOrderDelivered();
	}

	public ShippingWorkFlow createWorkFlowConnection(String id) {
		WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(ShippingWorkFlow.QUEUE_NAME)
				.setWorkflowId("Order_" + id).build();
		return workflowClient.newWorkflowStub(ShippingWorkFlow.class, options);
	}

}
