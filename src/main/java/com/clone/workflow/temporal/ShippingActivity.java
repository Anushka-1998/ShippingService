package com.clone.workflow.temporal;

import com.clone.workflow.domain.RouteDTO;
import com.clone.workflow.domain.RouteInfo;
import com.clone.workflow.exception.ExternalServiceCallException;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.util.RouteMatcher;
import reactor.core.publisher.Mono;

import java.util.List;

@ActivityInterface
public interface ShippingActivity {

	@ActivityMethod
	RouteInfo getRouteDetails(String source, String destination);

	@ActivityMethod
	Double getEquipmentAvailability(String source, String typeOfContainer);

	@ActivityMethod
	List<RouteDTO> getSpaceAvailability(List<RouteDTO> routeDTOList, Double noOfContainers);

}
