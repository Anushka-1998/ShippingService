package com.clone.workflow.controller;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShippingController {


    private final ShippingService orderService;

    /**
     * This methods sends Od3cpRequestInfo as request Body and sends Mono<ProductDetails> as response
     *
     * @param requestInfo od3cp details of the request
     * @return Mono<ProductDetails>
     */
    @PostMapping("/bookProductSendData")
    public Mono<ProductDetails> bookProductSendData(@RequestBody Od3cpRequestInfo requestInfo) {
        String requestId = UUID.randomUUID().toString();
        requestInfo.setRequestId(requestId);
        log.info("Request Details : {}", requestInfo);
        return orderService.bookProductSendData(requestInfo);
    }

    /**
     * This methods sends Od3cpRequestInfo as request Body and sends String as response
     *
     * @param requestInfo od3cp details of the request
     * @return "Booking done"
     */

    @PostMapping("/bookProductSendString")
    public Mono<ProductDetails> bookProductSendString(@RequestBody Od3cpRequestInfo requestInfo) {
        String requestId = UUID.randomUUID().toString();
        requestInfo.setRequestId(requestId);
        log.info("Request Details : {}", requestInfo);
        return orderService.bookProductSendString(requestInfo);

    }


    /**
     * This method takes in productId as input and sends Mono<ProductDetails> as response
     *
     * @param productId id of the product
     * @return Mono<ProductDetails>
     */
    @GetMapping("/getProductDetails")
    public Mono<ProductDetails> getProductDetails(@RequestParam("productId") String productId) {
        log.info("get ProductDetails for productId : {}", productId);
        return orderService.getProduct(productId);
    }


    /**
     * This method takes in route and save using saga pattern
     *
     * @param requestInfo contains route info
     * @return Mono<ProductDetails>
     */
    @PostMapping("/saveRequestDetails")
    public Mono<String> saveProduct(@RequestBody Od3cpRequestInfo requestInfo) {
        return orderService.saveRequest(requestInfo);
    }
}
