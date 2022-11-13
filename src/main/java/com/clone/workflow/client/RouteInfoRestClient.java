package com.clone.workflow.client;


import com.clone.workflow.domain.RouteInfo;
import com.clone.workflow.exception.ExternalServiceCallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

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
        return webclient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(RouteInfo.class)
                .onErrorMap(error -> {
                    throw new ExternalServiceCallException("exception while calling route service ..."+error.getMessage());
                });
    }
}
