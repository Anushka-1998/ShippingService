package com.clone.workflow.temporal;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.producer.KafkaProducer;
import com.clone.workflow.repository.Od3cpRequestRepository;
import io.temporal.client.ActivityCompletionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RouteActivityImpl implements RouteActivity {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private Od3cpRequestRepository od3cpRequestRepository;

    private ActivityCompletionClient activityCompletionClient;

    public RouteActivityImpl(ActivityCompletionClient activityCompletionClient) {
        this.activityCompletionClient = activityCompletionClient;

    }

    @Override
    public void saveRouteStatusInDb(Od3cpRequestInfo od3cpRequestInfo) {
        od3cpRequestRepository.save(od3cpRequestInfo).block();
    }

    @Override
    public String SendSuccessEvent(Od3cpRequestInfo od3cpRequestInfo) {

        return kafkaProducer.publishEvent("successfully saved route", "1", "SUCCESS", "id_token", "success_topic")
                .block();
    }

    @Override
    public void updateRouteStatusInDb(Od3cpRequestInfo od3cpRequestInfo) {
        od3cpRequestRepository.findById(od3cpRequestInfo.getRequestId())
                .map(res -> {
                    res.setStatus("SUSPENDED");
                    return res;
                })
                .flatMap(res -> od3cpRequestRepository.save(res)).block();
    }

    @Override
    public void SendFailEvent(Od3cpRequestInfo od3cpRequestInfo) {
        kafkaProducer.publishEvent("failed to  save route", "1", "FAILURE", "id_token", "failure_topic")
                .subscribe();

    }

}
