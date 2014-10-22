package org.beesa.atmosphere.samples;
/**
 * 
 */


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author sarat
 *
 */

@Path("/")
@AtmosphereService(dispatch = false,
interceptors = {AtmosphereResourceLifecycleInterceptor.class},
path = "/gossip",
servlet = "com.sun.jersey.spi.spring.container.servlet.SpringServlet",
 atmosphereConfig="org.atmosphere.websocket.WebSocketProtocol=org.atmosphere.websocket.protocol.SimpleHttpProtocol")
public class GossipHandler {

	Logger logger = LoggerFactory.getLogger(GossipHandler.class);

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deviceLogin(DeviceDetails deviceDetails , @Context AtmosphereResource requestingResource) {
		String originalWSConnectionUUId = (String) requestingResource.getRequest().getAttribute(ApplicationConfig.SUSPENDED_ATMOSPHERE_RESOURCE_UUID);
		logger.info("Resource uuid : {}, original resource Id ? {} ", requestingResource.uuid()
				, originalWSConnectionUUId);
		AtmosphereResource originalWSResource = requestingResource.getAtmosphereConfig().resourcesFactory().find(originalWSConnectionUUId);
		originalWSResource.addEventListener(new AtmosphereResourceEventListener() {
			
			Logger logger = LoggerFactory.getLogger(GossipHandler.class);
			@Override
			public void onHeartbeat(AtmosphereResourceEvent event) {
				logger.info("Heartbeat : {}", event);
			}
			
			@Override
			public void onThrowable(AtmosphereResourceEvent event) {
				logger.info(String.valueOf(event));
			}
			
			@Override
			public void onSuspend(AtmosphereResourceEvent event) {
				logger.info("Suspending resource : {} ", event);
			}
			
			@Override
			public void onResume(AtmosphereResourceEvent event) {
				logger.info("Resuming resource : {} ", event);
			}
			
			@Override
			public void onPreSuspend(AtmosphereResourceEvent event) {
				logger.info("Pre-suspend resource step : {} ", event);
			}
			
			@Override
			public void onDisconnect(AtmosphereResourceEvent event) {
				logger.info("Disconnected resource : {} ", event);
			}
			
			@Override
			public void onClose(AtmosphereResourceEvent event) {
				logger.info("Closed resource : {} ", event);
			}
			
			@Override
			public void onBroadcast(AtmosphereResourceEvent event) {
				logger.info("Broadcasting to resource : {} ", event);
			}
		});
		logger.info("Original websocket connection is obtained. Details :  {}", originalWSResource);
		return Response.ok("Successfully logged in.").build();
	}
	
}
