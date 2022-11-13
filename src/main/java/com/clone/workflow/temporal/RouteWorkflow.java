package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface RouteWorkflow {
    String SAVE_PRODUCT_QUEUE = "SAVE-PRODUCT-QUEUE";


    @WorkflowMethod(name = "save Request")
    void saveProductWorkflow(Od3cpRequestInfo routeInfo);
}
