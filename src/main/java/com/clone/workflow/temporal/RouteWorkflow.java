package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface RouteWorkflow {
    String SAVE_PRODUCT_QUEUE = "SAVE-PRODUCT-QUEUE";


    @WorkflowMethod(name = "save Request")
    void saveProductWorkflow(Od3cpRequestInfo routeInfo);

    @SignalMethod
    void startKafkaEvent(Od3cpRequestInfo routeInfo);

    @SignalMethod
    void resumeKafkaEvent(Od3cpRequestInfo routeInfo);
}
