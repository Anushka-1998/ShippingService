package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import static java.util.Optional.ofNullable;


public class RouteWorkflowImpl implements RouteWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(2).build())
            .build();
    private final RouteActivity activity = Workflow.newActivityStub(RouteActivity.class, options);

    private final Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(true).build());


    @Override
    public void saveProductWorkflow(Od3cpRequestInfo routeInfo) {

        saveProductCompensate(routeInfo);
        activity.saveRouteStatusInDb(routeInfo);

        ofNullable(activity.SendSuccessEvent(routeInfo))
                .filter(res -> res.equalsIgnoreCase("ERROR"))
                .ifPresent(res -> saga.compensate());
    }

    private void saveProductCompensate(Od3cpRequestInfo routeInfo) {
        saga.addCompensation(() -> {
            activity.updateRouteStatusInDb(routeInfo);
            activity.SendFailEvent(routeInfo);
        });
    }
}
