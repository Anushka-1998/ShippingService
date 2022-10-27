package com.clone.workflow.client;


import com.clone.workflow.domain.RouteInfo;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class RouteInfoRestClient {

    @Autowired
    private WebClient webclient;

    @Value("${restClient.routeInfoUrl}")
    private String routeInfoUrl;

    public Mono<RouteInfo> retrieveRouteInfo(String source, String destination)  {

        log.info("I am able to reach before webclient");
        var url = UriComponentsBuilder.fromHttpUrl(routeInfoUrl)
                .queryParam("source",source)
                .queryParam("destination", destination)
                .buildAndExpand().toUriString();
        log.info("review url is {}",url);

        return webclient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(RouteInfo.class);
    }



}
