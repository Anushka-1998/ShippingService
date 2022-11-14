package com.clone.workflow.temporal;

import java.time.Duration;
import java.util.List;

import com.clone.workflow.domain.Od3cpRequestInfo;
import com.clone.workflow.domain.ProductDetails;
import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.domain.RouteInfo;
import com.clone.workflow.exception.ExternalServiceCallException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ShippingWorkflowImpl implements ShippingWorkFlow {

	private final ActivityOptions options = ActivityOptions.newBuilder().setScheduleToCloseTimeout(Duration.ofSeconds(5))
			.setRetryOptions(RetryOptions.newBuilder()
					.setMaximumAttempts(1).build())
			.build();
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
		RouteInfo possibleRoutes = null;
		Double equipmentAvailability = null;
		try {
			//commenting async call for error handling
//			possibleRoutes = Async.function(activity::getRouteDetails, requestInfo.getSource(), requestInfo.getDestination());
//			equipmentAvailability = Async.function(activity::getEquipmentAvailability,requestInfo.getSource(),requestInfo.getContainerType());

			possibleRoutes = activity.getRouteDetails(requestInfo.getSource(), requestInfo.getDestination());
			equipmentAvailability = activity.getEquipmentAvailability(requestInfo.getSource(),requestInfo.getContainerType());

		}

		catch (ExternalServiceCallException e) {
				throw new ExternalServiceCallException("Exception caught while processing booking service workflow, activity name : getRouteActivity");
		}
		List<RouteDTO> routeDTOList = possibleRoutes.getRouteList();
		List<RouteDTO> availRouteList = routeDTOList;

		if(!routeDTOList.isEmpty() && equipmentAvailability >= requestInfo.getNoOfContainers()){
			log.info("Both routes and equipment is available");
			log.info("Calling space Availability");
			try {
				availRouteList = activity.getSpaceAvailability(routeDTOList,requestInfo.getNoOfContainers());
			} catch (ExternalServiceCallException e) {
				throw new ExternalServiceCallException("Exception caught while processing workflow "+e.getMessage());
			}
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
