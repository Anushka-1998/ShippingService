package com.clone.workflow.temporal;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.domain.RouteInfo;
import com.clone.workflow.repository.ProductDetailRepository;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ShippingWorkflowImpl implements ShippingWorkFlow {

	private final RetryOptions retryoptions = RetryOptions.newBuilder().setInitialInterval(Duration.ofSeconds(1))
			.setMaximumInterval(Duration.ofSeconds(100)).setBackoffCoefficient(2).setMaximumAttempts(500).build();
	private final ActivityOptions options = ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(30))
			.setRetryOptions(retryoptions).build();

	private final ShippingActivity activity = Workflow.newActivityStub(ShippingActivity.class, options);

	public boolean isOrderConfirmed = false;

	public boolean isOrderPickedUp = false;

	public boolean isOrderDelivered = false;

	private ProductDetailRepository productDetailRepository;


	private ProductDetails productDetails;


	@Override
	public ProductDetails startApprovalWorkflow(Od3cpRequestInfo requestInfo)  {
		Promise<RouteInfo> possibleRoutes  = Async.function(activity::getRouteDetails, requestInfo.getSource(), requestInfo.getDestination());
		Promise<Double> equipmentAvailability = Async.function(activity::getEquipmentAvailability,requestInfo.getSource(),requestInfo.getContainerType());
		List<RouteDTO> routes = possibleRoutes.get().getRouteList();
		List<RouteDTO> routeDTOList = null;

		//Workflow.sleep(5000);
		if(!routes.isEmpty() && equipmentAvailability.get() >= requestInfo.getNoOfContainers()){
			log.info("calling space Availability");
			routeDTOList = activity.getSpaceAvailability(routes,requestInfo.getNoOfContainers());
		}

		log.info("***** Waiting for space availability details");
		log.info("***** Please wait till we assign a delivery executive ");

		productDetails = ProductDetails.builder()
				  .productId(requestInfo.getRequestId())
				  .source(requestInfo.getSource()).destination(requestInfo.getDestination())
				  .containerType(requestInfo.getContainerType())
				  .containerSize(requestInfo.getContainerSize())
				  .availableRoutes(routeDTOList).build();
		log.info("productDetails : {}",productDetails);
		return productDetails;

		/* Mono<ProductDetails> productDetails = productDetailRepository.save(ProductDetails.builder()
				.productId(requestInfo.getRequestId())
				.source(requestInfo.getSource()).destination(requestInfo.getDestination())
				.containerType(requestInfo.getContainerType())
				.containerSize(requestInfo.getContainerSize())
				.availableRoutes(Arrays.asList()).build());*/



		// System.out.println("Prod : "+productDetails.block());

	//	possibleRoutes.get().subscribe(e->e.getRouteList().stream().forEach(f-> System.out.println(f.getVesselSize())));

		//activity.getRouteDetails(requestInfo.getSource(), requestInfo.getDestination());

	//	activity.getEquipmentAvailability(requestInfo.getSource(),requestInfo.getContainerType());

	//	Workflow.await(() -> isOrderConfirmed);
		

		//Workflow.await(() -> isOrderPickedUp);
		
	//	Workflow.await(() -> isOrderDelivered);

	}



	@Override
	public ProductDetails getProductUsingQuery() {
	//	log.info("ProductDetails : {}",productDetails);
		return productDetails;
	}

	@Override
	public void signalOrderAccepted() {
		activity.setOrderAccepted();
		this.isOrderConfirmed = true;
	}

	@Override
	public void signalOrderPickedUp() {
		activity.setOrderPickedUp();
		this.isOrderPickedUp = true;
	}

	@Override
	public void signalOrderDelivered() {
		activity.setOrderDelivered();
		this.isOrderDelivered = true;
	}

}
