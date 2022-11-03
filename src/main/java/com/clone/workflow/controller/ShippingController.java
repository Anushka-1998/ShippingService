package com.clone.workflow.controller;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clone.workflow.service.ShippingService;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class ShippingController {

    @Autowired
    ShippingService orderService;

    /**
     * This methods sends Od3cpRequestInfo as request Body and sends Mono<ProductDetails> as response
     * @param requestInfo
     * @return Mono<ProductDetails>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/bookProductSendData")
    public Mono<ProductDetails> bookProductSendData(@RequestBody Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
        String requestId = UUID.randomUUID().toString();
        requestInfo.setRequestId(requestId);
        log.info("Request Details : {}",requestInfo);
        return orderService.bookProductSendData(requestInfo);
    }

    /**
     * This methods sends Od3cpRequestInfo as request Body and sends String as response
     * @param requestInfo
     * @return "Booking done"
     * @throws ExecutionException
     * @throws InterruptedException
     */

	@PostMapping("/bookProductSendString")
	public Mono<ProductDetails> bookProductSendString(@RequestBody Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
		String requestId = UUID.randomUUID().toString();
		requestInfo.setRequestId(requestId);
        log.info("Request Details : {}",requestInfo);
		String bookingString = orderService.bookProductSendString(requestInfo);
		return bookingString;
	}


    /**
     * This method takes in productId as input and sends Mono<ProductDetails> as response
     * @param productId
     * @return Mono<ProductDetails>
     */
    @GetMapping("/getProductDetails")
    public Mono<ProductDetails> getProductDetails(@RequestParam("productId") String productId) {
        log.info("get ProductDetails for productId : {}",productId);
        return orderService.getProduct(productId);
    }

}
