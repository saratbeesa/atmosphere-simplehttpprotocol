package org.beesa.atmosphere.client;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Encoder;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.Request.METHOD;
import org.atmosphere.wasync.Request.TRANSPORT;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.impl.AtmosphereClient;
import org.atmosphere.wasync.impl.AtmosphereRequest.AtmosphereRequestBuilder;
import org.beesa.atmosphere.samples.DeviceDetails;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WASyncClientTest {

	static Logger logger = LoggerFactory.getLogger(WASyncClientTest.class);
	
	public static void main(String[] args) {
		AtmosphereClient client = ClientFactory.getDefault().newClient(AtmosphereClient.class);
		final Socket wsConn = client.create();
		
		wsConn.on(Event.TRANSPORT, new Function<TRANSPORT>() {
		    @Override
		    public void on(TRANSPORT t) {
		        // The Connection is opened
		    	logger.info("Using Transport : "+t);
		    }
		}).on(Event.MESSAGE, new Function<String>() {

			@Override
			public void on(String t) {
				logger.info("======Login Response===========");
				logger.info(t);
			}
		}).on(Event.CLOSE.name(), new Function<String>() {
            @Override
            public void on(String t) {
                logger.info("Connection closed");
            }
        }).on(Event.OPEN.name(), new Function<String>() {
            @Override
            public void on(String t) {
                logger.info("COnnection is open for messages now : "+t);
            }
        }).on(Event.STATUS.name(), new Function<String>() {
            @Override
            public void on(String t) {
                logger.info("Response Status : "+t);
            }
        }).on(new Function<Throwable>() {
            @Override
            public void on(Throwable t) {
                logger.error("",t);
            }
        });
		
		AtmosphereRequestBuilder requestBldr = client.newRequestBuilder();
		Encoder<DeviceDetails, String> encoderForRequest = new Encoder<DeviceDetails, String>() {

			@Override
			public String encode(DeviceDetails s) {
				try {
					StringBuilder buffer = new StringBuilder();
					return buffer.append("@@/login@@\n").append(new ObjectMapper().writeValueAsString(s)).toString();
				}
				catch (IOException e) {
					logger.error("",e);
					return "";
				}
			}
		};
		requestBldr.uri("http://localhost:8080/atmosphere-protocol-sample/ws").method(METHOD.GET).transport(TRANSPORT.WEBSOCKET)
		.encoder(encoderForRequest);
		
		final Request loginRequest = requestBldr.build();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					wsConn.open(loginRequest);
				} catch (IOException e) {
					logger.error("",e);
				}
			}
		}).start();
		
		// await for the response.
		try {
			Thread.sleep(2000);
			DeviceDetails user = new DeviceDetails();
			user.setUsername("abcdf@mydomain.com");
			user.setUuid(UUID.randomUUID().toString());
			Future<Socket> result = wsConn.fire(user).fire(user);
			result.get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			 logger.error("",e);
		}
		logger.info("Message has been successfully dispatched.");
	}
}
