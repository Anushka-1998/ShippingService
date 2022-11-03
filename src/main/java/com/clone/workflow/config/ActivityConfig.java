package com.clone.workflow.config;

import com.clone.workflow.temporal.ShippingActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class ActivityConfig {

    ActivityOptions activityOptions =
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .setRetryOptions(RetryOptions.newBuilder()
                    // max attempts set for demo purposes
                    .setMaximumAttempts(2)
                    .build())
                    .build();
    ShippingActivity activity = Workflow.newActivityStub(ShippingActivity.class, activityOptions);
}
