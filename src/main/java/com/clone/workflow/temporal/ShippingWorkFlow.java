package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ShippingWorkFlow {

	public static final String QUEUE_NAME = "MAERSK-SHIPPING-QUEUE";

	@WorkflowMethod
	ProductDetails startApprovalWorkflow(Od3cpRequestInfo requestInfo);

	@QueryMethod
	ProductDetails getProductUsingQuery();

	@SignalMethod
	void signalOrderAccepted();

	@SignalMethod
	void signalOrderPickedUp();

	@SignalMethod
	void signalOrderDelivered();

}
