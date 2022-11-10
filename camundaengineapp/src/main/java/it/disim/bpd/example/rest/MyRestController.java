package it.disim.bpd.example.rest;

import java.util.Random;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

	@Autowired
	private RuntimeService runtimeService;

	@GetMapping("/message-1/{messagePathVar}")
	public ResponseEntity<String> message1Method(@PathVariable("messagePathVar") String pathVar) {

		System.out.println("Received REST request to start a new process");
		
		String businessKey = generateRandBusinessKey();

		runtimeService.createMessageCorrelation("message-1")
				.processInstanceBusinessKey(businessKey)
				.setVariable("receivedVar", pathVar)
				.correlate();

		return ResponseEntity.ok(businessKey);
	}

	@GetMapping("/message-2/{businessKey}")
	public ResponseEntity<String> message2Method(@PathVariable String businessKey) {

		System.out.println("Received REST request resume process " + businessKey);

		runtimeService.createMessageCorrelation("message-2")
				.processInstanceBusinessKey(businessKey)
				.correlate();

		return ResponseEntity.ok("Process " + businessKey + " resumed after receiving message-2");
	}

	@GetMapping("/message-3")
	public ResponseEntity<String> receiveMessageAndGetReponse() {

		MessageCorrelationResultWithVariables result = runtimeService
				.createMessageCorrelation("message-3")
				.correlateWithResultAndVariables(false);

		String resultVar = (String) result.getVariables().get("soapResponse");

		return ResponseEntity.ok(resultVar);
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
