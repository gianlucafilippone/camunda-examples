package it.disim.bpd.example.services;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("helloWorldService")
public class HelloWorldService {

	public String helloWorld() {
		/*
		 * Here you can insert the code you want
		 */
		return "Variable returned to the process!";
	}

	public void someOtherMethod(DelegateExecution execution) {
		/*
		 * Here you can insert the code you want
		 */
		execution.setVariable("injectedVar", "This variable has been set into the method");
	}
}
