package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;


@ActivityInterface
public interface RouteActivity {

    @ActivityMethod
    void saveRouteStatusInDb(Od3cpRequestInfo od3cpRequestInfo);

    @ActivityMethod
    String SendSuccessEvent(Od3cpRequestInfo od3cpRequestInfo);

    @ActivityMethod
    void updateRouteStatusInDb(Od3cpRequestInfo od3cpRequestInfo);

    @ActivityMethod
    void SendFailEvent(Od3cpRequestInfo od3cpRequestInfo);

}
