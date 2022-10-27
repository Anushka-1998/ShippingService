package com.clone.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.clone.workflow.temporal.ShippingActivity;
import com.clone.workflow.temporal.ShippingWorkFlow;
import com.clone.workflow.temporal.ShippingWorkflowImpl;

import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

@SpringBootApplication
public class SwiggyCloneApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(SwiggyCloneApplication.class, args);
		WorkerFactory factory = appContext.getBean(WorkerFactory.class);
		ShippingActivity signUpActivity = appContext.getBean(ShippingActivity.class);
		Worker worker = factory.newWorker(ShippingWorkFlow.QUEUE_NAME);
		worker.registerWorkflowImplementationTypes(ShippingWorkflowImpl.class);
		worker.registerActivitiesImplementations(signUpActivity);
		factory.start();
	}

}
