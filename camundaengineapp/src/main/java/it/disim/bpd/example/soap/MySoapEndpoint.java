package it.disim.bpd.example.soap;

import java.util.Random;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import it.disim.bpd.example.domain.MyOperationRequest;
import it.disim.bpd.example.domain.MyOperationResponse;

@Endpoint
public class MySoapEndpoint {

	private static final String NAMESPACE_URI = "http://bpd.disim.univaq.it/";

	@Autowired
	private RuntimeService runtimeService;

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "myOperationRequest")
	@ResponsePayload
	public MyOperationResponse myOperation(@RequestPayload MyOperationRequest request) {
		MyOperationResponse response = new MyOperationResponse();

		System.out.println("Received SOAP message: myOperationRequest");
		
		String businessKey = generateRandBusinessKey();

		runtimeService.createMessageCorrelation("message-1")
				.processInstanceBusinessKey(businessKey)
				.setVariable("recPayload", request)
				.correlate();

		response.setReturnValue(businessKey);
		return response;
	}

	private static String generateRandBusinessKey() {
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();

	    return random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}

}
