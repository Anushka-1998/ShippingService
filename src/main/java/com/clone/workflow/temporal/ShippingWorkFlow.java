package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.domain.RouteInfo;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ShippingWorkFlow {

	String QUEUE_NAME = "MAERSK-SHIPPING-QUEUE";

	@WorkflowMethod(name = "search")
	ProductDetails startWorkflow(Od3cpRequestInfo requestInfo);

}
