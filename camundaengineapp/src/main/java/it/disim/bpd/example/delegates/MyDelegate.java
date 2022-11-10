package it.disim.bpd.example.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("myDelegate")
public class MyDelegate implements JavaDelegate {

	private int var = 0;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		/*
		 * Here you can insert the code you want
		 */
		System.out.println("Hello World!");
		execution.setVariable("myVariable", ++var);
	}

}
