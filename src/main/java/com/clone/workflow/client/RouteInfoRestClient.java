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

    /**
     * This method calls routeInfoService using webClient call
     * @param source
     * @param destination
     * @return
     */

    public Mono<RouteInfo> retrieveRouteInfo(String source, String destination)  {
        log.info("Inside retrieveRouteInfo() method");
        var url = UriComponentsBuilder.fromHttpUrl(routeInfoUrl)
                .queryParam("source",source)
                .queryParam("destination", destination)
                .buildAndExpand().toUriString();
        log.info("routeInfo URL is : {}",url);
//        return webclient
//                .get()
//                .uri(url)
//                .retrieve()
//                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
//                        clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
//                .onStatus(HttpStatus.NOT_FOUND::equals,
//                        clientResponse -> clientResponse.bodyToMono(String.class).map(Exception::new))
//                .bodyToMono(RouteInfo.class);

        return webclient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(RouteInfo.class)
                .onErrorMap(error -> {
                    throw new RuntimeException("Exception caught while calling route service ..."+error.getMessage());
                });
    }
}
