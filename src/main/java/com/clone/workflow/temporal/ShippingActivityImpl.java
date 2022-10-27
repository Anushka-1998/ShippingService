package com.clone.workflow.temporal;

import com.clone.workflow.client.EquipmentAvailabilityRestClient;
import com.clone.workflow.client.RouteInfoRestClient;
import com.clone.workflow.client.SpaceAvailbilityRestClient;
import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.domain.RouteInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class ShippingActivityImpl implements ShippingActivity {
	@Autowired
	private RouteInfoRestClient routeInfoRestClient;

	@Autowired
	private EquipmentAvailabilityRestClient equipmentAvailabilityRestClient;

	@Autowired
	private SpaceAvailbilityRestClient spaceAvailbilityRestClient;

	@Override
	public RouteInfo getRouteDetails(String source, String destination)  {

		var routeInfoMono = routeInfoRestClient.retrieveRouteInfo(source, destination);

		 routeInfoMono.subscribe(name->{
			log.info("Name is {}",name);
		});

		 RouteInfo routeInfo = routeInfoMono.block();

		//container availablity
		System.out.println("Retrieved the route details");

		return routeInfo;
	}

	@Override
	public Double getEquipmentAvailability(String source, String typeOfContainer) {

		var containerInfo = equipmentAvailabilityRestClient
				.retrieveEquipmentAvailability(source,typeOfContainer);

		containerInfo.subscribe(noOfContainers->{
			log.info("ContainerNo is {} ",noOfContainers);
		});

		Double containerSize = containerInfo.block();

		//container availablity

		System.out.println("Retrieved the conatiner details");
		return containerSize;

	}

	@Override
	public List<RouteDTO> getSpaceAvailability(List<RouteDTO> routeDTOList,Double noOfContainers) {
		Flux<RouteDTO> availableRoutes = spaceAvailbilityRestClient
				.retrieveSpaceAvailability(routeDTOList,noOfContainers);

		List<RouteDTO> listOfRouteDTO = availableRoutes.collectList().block();
		return listOfRouteDTO;
	}


	@Override
	public void setOrderAccepted() {
		System.out.println("***** Restaurant has accepted your order");
	}

	@Override
	public void setOrderPickedUp() {
		System.out.println("***** Order has been picked up");
	}

	@Override
	public void setOrderDelivered() {
		System.out.println("***** Order Delivered");
	}

}
