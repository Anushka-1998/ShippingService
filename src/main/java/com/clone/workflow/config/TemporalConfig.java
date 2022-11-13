package com.clone.workflow.config;

import com.clone.workflow.temporal.RouteActivityImpl;
import com.clone.workflow.temporal.ShippingActivityImpl;
import io.temporal.client.ActivityCompletionClient;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    private static final String TEMPORAL_SERVER = "127.0.0.1:7233";

    private static final String TEMPORAL_NAMESPACE = "default";

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs
                .newInstance(WorkflowServiceStubsOptions.newBuilder().setTarget(TEMPORAL_SERVER).build());
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
        return WorkflowClient.newInstance(workflowServiceStubs,
                WorkflowClientOptions.newBuilder().setNamespace(TEMPORAL_NAMESPACE).build());
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        return WorkerFactory.newInstance(workflowClient);
    }

    @Bean
    public ShippingActivityImpl signUpActivity() {
        ActivityCompletionClient activityCompletionClient = workflowClient(workflowServiceStubs()).newActivityCompletionClient();
        return new ShippingActivityImpl(activityCompletionClient);
    }

    @Bean
    public RouteActivityImpl routeSignUpActivity() {
        ActivityCompletionClient activityCompletionClient = workflowClient(workflowServiceStubs()).newActivityCompletionClient();
        return new RouteActivityImpl(activityCompletionClient);
    }


}
