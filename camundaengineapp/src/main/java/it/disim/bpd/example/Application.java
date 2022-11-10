package it.disim.bpd.example;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EnableProcessApplication
public class Application {

	@Autowired
	private RuntimeService runtimeService;

	public static void main(String... args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener
	public void processPostDeploy(PostDeployEvent event) {
		runtimeService.startProcessInstanceByKey("MyAutoStartedProcess");
	}

}