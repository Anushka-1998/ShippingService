package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Saga;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.Duration;

import static java.util.Optional.ofNullable;

@Slf4j
public class RouteWorkflowImpl implements RouteWorkflow {

    private Od3cpRequestInfo startEventPayload;
    private Od3cpRequestInfo resumeEventpayload;
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
        Workflow.await(() -> !ObjectUtils.isEmpty(startEventPayload));
        activity.saveRouteStatusInDb(routeInfo);
        log.info("workflow will wait for signal now");
        Workflow.await(() -> !ObjectUtils.isEmpty(resumeEventpayload));

        log.info("workflow resume now based on signal");
        ofNullable(activity.SendSuccessEvent(routeInfo))
                .filter(res -> res.equalsIgnoreCase("ERROR"))
                .ifPresent(res -> saga.compensate());

    }

    @Override
    public void startKafkaEvent(Od3cpRequestInfo routeInfo) {

        this.startEventPayload = routeInfo;

    }

    @Override
    public void resumeKafkaEvent(Od3cpRequestInfo routeInfo) {
        this.resumeEventpayload = routeInfo;

    }

    private void saveProductCompensate(Od3cpRequestInfo routeInfo) {
        saga.addCompensation(() -> {
            activity.updateRouteStatusInDb(routeInfo);
            activity.SendFailEvent(routeInfo);
        });
    }


}
