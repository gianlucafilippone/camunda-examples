package it.univaq.disim.bpm.camunda.externaltask;

import java.util.Collections;

import org.camunda.bpm.client.ExternalTaskClient;

public class CustomTaskWorker {


	public static void main(String[] args) {
		ExternalTaskClient client = ExternalTaskClient.create()
				.baseUrl("http://localhost:8080/engine-rest")
				.asyncResponseTimeout(1000)
				.build();

		client.subscribe("My External Task").handler((externalTask, externalTaskService) -> {
			System.out.println("Hello World!");
			externalTaskService.complete(externalTask, Collections.singletonMap("new-variable", "This is the new variable Content!"));
		}).open();
	}

}
