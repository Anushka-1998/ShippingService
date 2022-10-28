package com.clone.workflow.temporal;

import com.clone.workflow.client.EquipmentAvailabilityRestClient;
import com.clone.workflow.client.RouteInfoRestClient;
import com.clone.workflow.client.SpaceAvailbilityRestClient;
import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.domain.RouteInfo;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionClient;
import io.temporal.failure.ActivityFailure;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Slf4j
public class ShippingActivityImpl implements ShippingActivity {

	private final ActivityCompletionClient activityCompletionClient;

	@Autowired
	private RouteInfoRestClient routeInfoRestClient;

	@Autowired
	private EquipmentAvailabilityRestClient equipmentAvailabilityRestClient;

	@Autowired
	private SpaceAvailbilityRestClient spaceAvailbilityRestClient;

	public ShippingActivityImpl(ActivityCompletionClient activityCompletionClient) {
		this.activityCompletionClient = activityCompletionClient;
	}

	@Override
	public RouteInfo getRouteDetails(String source, String destination)  {

     log.info("Inside getRouteDetails() for source : {} | destination : {}",source,destination);
			ActivityExecutionContext context = Activity.getExecutionContext();
			byte[] taskToken = context.getTaskToken();
			ForkJoinPool.commonPool().execute(() -> getPossibleRoutesAsync(taskToken, source, destination));
			context.doNotCompleteOnReturn();

		return RouteInfo.builder().build();
	}


	public void getPossibleRoutesAsync(byte[] taskToken, String source, String destination) {

		log.info("Inside getPossibleRoutesAsync() method");
		var routeInfoMono = routeInfoRestClient.retrieveRouteInfo(source, destination);
		RouteInfo routeInfo = routeInfoMono.block();
		log.info("RouteInfo : {}",routeInfo);
		activityCompletionClient.complete(taskToken, routeInfo);

	}

	@Override
	public Double getEquipmentAvailability(String source, String typeOfContainer) {

		log.info("Inside getEquipmentAvailability() for source : {} | typeOfContainer : {}",source,typeOfContainer);
		ActivityExecutionContext context = Activity.getExecutionContext();
		byte[] taskToken = context.getTaskToken();
		ForkJoinPool.commonPool().execute(() -> getEquipmentAvailabilityAsync(taskToken, source, typeOfContainer));
		context.doNotCompleteOnReturn();
		return 0.0;

	}

	public void getEquipmentAvailabilityAsync(byte[] taskToken, String source, String typeOfContainer){

		log.info("Inside getEquipmentAvailabilityAsync()");
		var containerInfo = equipmentAvailabilityRestClient
					.retrieveEquipmentAvailability(source, typeOfContainer);

		Double containerSize = containerInfo.block();
		log.info("containerSize : {} for source : {}",containerSize,source);
		activityCompletionClient.complete(taskToken, containerSize);
	}

	@Override
	public List<RouteDTO> getSpaceAvailability(List<RouteDTO> routeDTOList,Double noOfContainers) {

		log.info("Inside getSpaceAvailability(), routeDTOList : {} | noOfContainers :{}",routeDTOList,noOfContainers);
		ActivityExecutionContext context = Activity.getExecutionContext();

		byte[] taskToken = context.getTaskToken();
		ForkJoinPool.commonPool().execute(() -> getSpaceAvailabilityAsync(taskToken, routeDTOList, noOfContainers));
		context.doNotCompleteOnReturn();

		return Arrays.asList(RouteDTO.builder().build());
	}

	public void getSpaceAvailabilityAsync(byte[] taskToken, List<RouteDTO> routeDTOList,Double noOfContainers){

		log.info("Inside getSpaceAvailabilityAsync() method");
		Flux<RouteDTO> availableRoutes = spaceAvailbilityRestClient
				.retrieveSpaceAvailability(routeDTOList,noOfContainers);

		List<RouteDTO> availSpaceRouteDTO = availableRoutes.collectList().block();
		log.info("availSpaceRouteDTO : {}",availSpaceRouteDTO);
		activityCompletionClient.complete(taskToken, availSpaceRouteDTO);
	}
}
