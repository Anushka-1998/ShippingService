package com.clone.workflow.controller;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.clone.workflow.service.ShippingService;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class ShippingController {

	@Autowired
	ShippingService orderService;



	@PostMapping("/startWorkflow")
		public ProductDetails createOrder(@RequestBody Od3cpRequestInfo requestInfo) throws ExecutionException, InterruptedException {
		log.info("workflow starts");
		String requestId = UUID.randomUUID().toString();
		requestInfo.setRequestId(requestId);
		ProductDetails productDetails = orderService.placeOrder(requestInfo);
		return productDetails;
	}



	@GetMapping("/startWorkflow")
	public ProductDetails getProductDetails(@RequestParam("productId") String productId) {
		log.info("get workflow starts");
		ProductDetails productDetails = orderService.getProduct(productId);
		return productDetails;
	}







//	public String createOrder(@RequestParam("id") String id,
//							  @RequestParam("source") String source,
//							  @RequestParam("destination") String destination) {
//		log.info("workflow starts");
//		orderService.placeOrder(id, source, destination);
//		return "Order Placed";
//	}

	@PostMapping("/orderAccepted")
	public String orderAccepted(@RequestParam("id") String id) {
		orderService.makeOrderAccepted(id);
		return "Order Accepted";
	}

	@PostMapping("/orderPickedUp")
	public String orderPickedUp(@RequestParam("id") String id) {
		orderService.makeOrderPickedUp(id);
		return "Order Picked Up";
	}

	@PostMapping("/orderDelivered")
	public String orderDelivered(@RequestParam("id") String id) {
		orderService.makeOrderDelivered(id);
		return "Order Delivered";
	}
}
