package com.clone.workflow;

import com.clone.workflow.exception.ExternalServiceCallException;
import com.clone.workflow.temporal.ShippingActivityImpl;
import io.temporal.client.WorkflowOptions;
import io.temporal.worker.WorkflowImplementationOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.clone.workflow.temporal.ShippingActivity;
import com.clone.workflow.temporal.ShippingWorkFlow;
import com.clone.workflow.temporal.ShippingWorkflowImpl;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.time.Duration;

@SpringBootApplication
public class ShippingApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(ShippingApplication.class, args);
		WorkerFactory factory = appContext.getBean(WorkerFactory.class);
		ShippingActivity signUpActivity = appContext.getBean(ShippingActivity.class);
	//	ShippingWorkFlow shippingWorkflow = appContext.getBean(ShippingWorkFlow.class);
		Worker worker = factory.newWorker(ShippingWorkFlow.QUEUE_NAME);

		WorkflowImplementationOptions options =
				WorkflowImplementationOptions.newBuilder()
						.setFailWorkflowExceptionTypes(NullPointerException.class)
						.setFailWorkflowExceptionTypes(RuntimeException.class)
						.setFailWorkflowExceptionTypes(ExternalServiceCallException.class)
						.build();

		worker.registerWorkflowImplementationTypes(options, ShippingWorkflowImpl.class);
		worker.registerActivitiesImplementations(signUpActivity);
//		worker.registerActivitiesImplementations(new ShippingActivityImpl());
		factory.start();
	}
}
