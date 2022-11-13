package com.clone.workflow.service;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.repository.ProductDetailRepository;
import com.clone.workflow.temporal.RouteWorkflow;
import com.clone.workflow.temporal.ShippingWorkFlow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingService {

    private final WorkflowClient workflowClient;

    private final ProductDetailRepository productDetailRepository;


    /**
     * This method initiates the workflow execution and saves the data in mongodb
     *
     * @param requestInfo od3cp details of the request
     * @return Mono<ProductDetails>
     */
    public Mono<ProductDetails> bookProductSendData(Od3cpRequestInfo requestInfo) {
        log.info("Inside bookProductSendData() method requestInfo : {}", requestInfo);
        long start = System.currentTimeMillis();
        ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());

        ProductDetails productDetail = null;
        try {
            productDetail = workflow.startWorkflow(requestInfo);
            log.info("Saving ProductDetails database : {}", productDetail);

        } catch (WorkflowException e) {
            log.error("***** Exception: " + e.getMessage());
            log.error("***** Cause: " + e.getCause().getClass().getName());
            log.error("***** Cause message: " + e.getCause().getMessage());
            long end = System.currentTimeMillis() - start;
            log.error("time taken to execute " + end);
        }

        if (!ObjectUtils.isEmpty(productDetail)) {
            return productDetailRepository.save(productDetail).log();
        }

        return null;
    }

    /**
     * This method fetches data from mongoDb respository based on productId
     *
     * @param productId selected product id
     * @return Mono<ProductDetails>
     */
    public Mono<ProductDetails> getProduct(String productId) {
        log.info("Inside getProduct() method for productId : {}", productId);
        Mono<ProductDetails> productDetails = productDetailRepository.findById(productId);
        log.info("ProductDetails coming from database : {}", productDetails);
        return productDetails;
    }

    /**
     * This method initiates the workflow execution and saves the data in mongodb
     *
     * @param requestInfo od3cp details of the request
     * @return String
     */
    public Mono<ProductDetails> bookProductSendString(Od3cpRequestInfo requestInfo){
        log.info("Inside bookProductSendString() method for requestInfo : {}", requestInfo);
        ShippingWorkFlow workflow = createWorkFlowConnection(requestInfo.getRequestId());
        CompletableFuture<ProductDetails> productDetails = WorkflowClient.execute(workflow::startWorkflow, requestInfo);
        return Mono.just(productDetails.getNow(ProductDetails.builder().build()));
    }


    /**
     * This method sets TaskQueue name, workflowId and returns ShippingWorkFlow as response
     *
     * @param id workflow id to be used
     * @return return workflow client
     */
    public ShippingWorkFlow createWorkFlowConnection(String id) {
        log.info("Inside createWorkFlowConnection() method id : {}", id);
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowRunTimeout(Duration.ofSeconds(50))
                .setTaskQueue(ShippingWorkFlow.QUEUE_NAME)
                .setWorkflowId("Workflow_" + id)
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(2).build())
                .build();
        return workflowClient.newWorkflowStub(ShippingWorkFlow.class, options);
    }

    public Mono<String> saveRequest(Od3cpRequestInfo routeInfo) {
        log.info("Inside createWorkFlowConnection() method id : {}", routeInfo.getRequestId());
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setWorkflowRunTimeout(Duration.ofSeconds(50))
                .setTaskQueue(RouteWorkflow.SAVE_PRODUCT_QUEUE)
                .setWorkflowId("Order_" + routeInfo.getRequestId())
                .setRetryOptions(RetryOptions.newBuilder()
                        .setMaximumAttempts(2).build())
                .build();
        var workflow = workflowClient.newWorkflowStub(RouteWorkflow.class, options);

        workflow.saveProductWorkflow(routeInfo);
        return Mono.just("saving initiated");
    }


}
