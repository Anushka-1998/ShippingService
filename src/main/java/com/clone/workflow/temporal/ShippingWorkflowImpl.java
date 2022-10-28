package com.clone.workflow.temporal;

import java.time.Duration;
import java.util.List;

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


@Slf4j
public class ShippingWorkflowImpl implements ShippingWorkFlow {

	private final RetryOptions retryoptions = RetryOptions.newBuilder().setInitialInterval(Duration.ofSeconds(1))
			.setMaximumInterval(Duration.ofSeconds(100)).setBackoffCoefficient(2).setMaximumAttempts(500).build();
	private final ActivityOptions options = ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(30))
			.setRetryOptions(retryoptions).build();
	private final ShippingActivity activity = Workflow.newActivityStub(ShippingActivity.class, options);


	/**
	 * This method marks the starting point of the workflow
	 * It calls routesService and equipmentAvailabilityService in parallel and aggregates
	 * their responses and calls space availability service
	 * @param requestInfo
	 * @return
	 */
	@Override
	public ProductDetails startWorkflow(Od3cpRequestInfo requestInfo)  {
		log.info("Inside startWorkflow() method");
		log.info("Calling getRouteDetails and equipmentAvailability service in parallel");
		Promise<RouteInfo> possibleRoutes  = Async.function(activity::getRouteDetails, requestInfo.getSource(), requestInfo.getDestination());
		Promise<Double> equipmentAvailability = Async.function(activity::getEquipmentAvailability,requestInfo.getSource(),requestInfo.getContainerType());
		List<RouteDTO> routeDTOList = possibleRoutes.get().getRouteList();
		List<RouteDTO> availRouteList = routeDTOList;

		if(!routeDTOList.isEmpty() && equipmentAvailability.get() >= requestInfo.getNoOfContainers()){
			log.info("Both routes and equipment is available");
			log.info("Calling space Availability");
			availRouteList = activity.getSpaceAvailability(routeDTOList,requestInfo.getNoOfContainers());
			return  ProductDetails.builder()
					.productId(requestInfo.getRequestId())
					.equipmentAvailability(true)
					.source(requestInfo.getSource()).destination(requestInfo.getDestination())
					.containerType(requestInfo.getContainerType())
					.containerSize(requestInfo.getContainerSize())
					.availableRoutes(availRouteList).build();
		}

		log.info("Either routes or equipment not available");

		return   ProductDetails.builder()
				.productId(requestInfo.getRequestId())
				.equipmentAvailability(false)
				.source(requestInfo.getSource()).destination(requestInfo.getDestination())
				.containerType(requestInfo.getContainerType())
				.containerSize(requestInfo.getContainerSize())
				.availableRoutes(routeDTOList).build();
	}
}
