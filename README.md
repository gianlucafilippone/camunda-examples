# Step-by-step Camunda guide to basic functions

## Download and installation
https://docs.camunda.org/get-started/quick-start/install/

#### Platform: Community edition

* Browse https://camunda.com/download/ -> Download "Camunda Platform Run"

* It will download an Apache Tomcat server with a Camunda Engine embedded
* When downloaded, unzip and run `startup.bat` (Windows users) or `startup.sh` (unix-like systems users)
* Browse http://localhost:8080
* Login with 'demo' 'demo' user. Username and password can be updated once logged in, or in `default.ymlt` file

#### Modeler:

* Browse https://camunda.com/download/modeler/ -> Download your preferred version

* Unzip and double-click to open

## Use the modeler
* Run the modeler
* Create a sample process:
   - Set the process name from properties
   - Create a script task, set javascript languages, write the script; `var system = java.lang.System; system.out.println("Hello World!");`
* Deploy:
   - Save on file
   - Click on the upload button on the modeler and follow the process

Example BPMN: `HelloWorldProcess.bpmn`

## Deploy and start a process
* Open Camunda dashboard -> Cockpit -> Process Definition
* Check if the deployed processes (if following this readme, you shoud see `HelloWorldProcess`)
* Start the process
   - Using REST API (you can access Swagger at http://localhost:8080/swaggerui
   - Right from the modeler
   - From Camunda Tasklist
* After starting the process, check the list of ended processes
   - From Camunda API: http://localhost:8080/engine-rest/history/process-instance?processDefinitionKey=HelloWorldProcess

## External Service Task
https://docs.camunda.org/get-started/quick-start/service-task/

* Create a process with the modeler
* Add a service task
   - Select "External" as implementation
   - Insert a topic (e.g., `My External Task`)
* Create a Java Maven project
   - Add external task client dependency:
      ```xml
            <dependency>
               <groupId>org.camunda.bpm</groupId>
               <artifactId>camunda-external-task-client</artifactId>
               <version>${camunda.external-task-client.version}</version>
            </dependency>
      .
      ```
   - Create a Java class with a main method
   - Add the code to handle the external task:
      ```java
            ExternalTaskClient client = ExternalTaskClient.create()
                  .baseUrl("http://localhost:8080/engine-rest")
                  .asyncResponseTimeout(1000)
                  .build();

            client.subscribe("My External Task").handler((externalTask, externalTaskService) -> {
               System.out.println("Hello World!");
               externalTaskService.complete(externalTask, Collections.singletonMap("new-variable", "This is the new variable Content!"));
            }).open();
      ```
* Start the Java program
* Start the process
   - Task worker will poll the external tasks and will execute those with the subscribed topic

Example BPMN: `ExternalTaskProcess.bpmn`
Java code: `external-task-worker/`

More on https://github.com/camunda/camunda-bpm-examples/tree/master/servicetask

## Spring Boot integration
https://docs.camunda.org/get-started/spring-boot/project-setup/

* Create new Java Maven project
   - Copy dependencies into the pom (see `/camundaengineapp/pom.xml`)
   - Create the main class of the SpringBoot application
   - Add application.yaml configuration file (see `/camundaengineapp/src/main/resources/application.yaml`)
   - Start application and check deployment on http://localhost8080/
* Alternatively, use the initializr: https://start.camunda.com
* Deploy a sample process
   - Possible to do it through the modeler (`camunda-bpm-spring-boot-starter-rest` required)
   - Inserting the BPMN file in the resources folder of the project
* Create the process application: https://docs.camunda.org/get-started/spring-boot/model/#start-a-process-instance-after-process-application-has-been-deployed
   - Add the @EnableProcessApplication annotation to the SpringBoot application class
   - Create an empty file called `processes.xml` in `resources/META-INF/`
   - Add a method with the `@EventListener` annotation:
      ```java
         @EventListener
         public void processPostDeploy(PostDeployEvent event) {
            runtimeService.startProcessInstanceByKey("MyAutoStartedProcess");
         }
      ```
* Rest API
   - By default, there are no Rest APIs to interact with to start the process etc.
   - Add the `camunda-bpm-spring-boot-starter-rest` dependency to the pom

Java Code: `camundaengineapp/`
BPMN process: `MyAutoStartedProcess.bpmn`

## Java Delegate
https://docs.camunda.org/manual/7.16/user-guide/process-engine/delegation-code/#java-delegate
* Create a process with a service task
   - Select `Java Class` as implementation
   - Enter the full name of a Java class (e.g., `it.disim.bpd.example.delegates.MyDelegate`)
* Create a new Java class within the project containing the Camunda engine
   - Create new package and new class with the same fully qualified name (e.g., `it.disim.bpd.example.delegates.MyDelegate`)
   - Implement the JavaDelegate interface (e.g., `public class MyDelegate implements JavaDelegate`)
   - Override the `execute(DelegateExecution execution)` method
* Start the Camunda engine and start the process

Example BPMN: `JavaDelegateProcess.bpmn`
Java Code: `MyDelegate.java`

## Make a REST call
https://github.com/camunda/camunda-bpm-examples/tree/master/servicetask/rest-service
* Create a new service task
   - Select `Connector` as implementation
   - Add `http-connector` as connector id
   - Add input parameters
      - `url`, as a string, assign it the value of the rest api url (e.g., `https://jsonplaceholder.typicode.com/posts/1`)
      - `method`, as a string give it the value "`GET`
   - Add output parameters
      - `headers`, as map and add the entry `"Accept, "application/json"`
      - `title`, as a string or expression, then use the Spin expression to extract a piece of data from the json: `${S(response).prop ("title")}`
* Add new dependencies in the pom
   - `connect`, `http-client`, `spin`, `json-dataformat` (see `/camundaengineapp/pom.xml`)
   - also add Camunda bom as dependency management
* Deploy and start process

Example BPMN: `HttpConnectorProcess.bpmn`
SOAP version: `SoapMessageProcess.bpmn` (`soap-http-client` dependency required)

 ## Receiving Messages
https://github.com/camunda/camunda-docs-manual/blob/master/content/reference/bpmn20/events/message-events.md
* Understanding the concept of message correlation for message events: https://docs.camunda.io/docs/components/modeler/bpmn/message-events/
   - Processes make subscribe for messages whose name corresponds to the names of the message events, on the basis of which correlation is made
   - When a message related to a start event is received, a new process instance is created
   - Correlation keys are used to control the creation of instances. The correlation is done according to the message name and the correlation key (not need for start events)
   - For intermediate catch events, the process stops to wait for a related message
   - The correlation key is contained within a variable in the process, and is used to make the correlation
* The logic for receiving messages is NOT integrated in Camunda, but must be implemented (using Spring Boot for example)
* Define a message in the bpmn
   - Create a new start event of type `message receive`
   - Enter an id, and then create a new global message. The name of the global message will be the name of the message to be correlated with (e.g., `message-1`)
* Create an API endpoint to receive the message
   - Create a new class annotated with `@RestController` with a method annotated with `@GetMapping`:
   - Add the code to do the message correlation
      ```java
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
      ```
* Run the application
   - Start the application through engine or endpoints
   - Call the REST API exposed at `http://localhost:8080/message-1/someMessageContent`
   - The request is expected to be captured by the endpoint that initiates the process correlating it with the message `message-1`
* For intermediate events, the message works in exactly the same way. There is no need for anything extra.
   - See `Message-2 received` event in `MessageCorrelationProcess.bpmn`

Example BPMN: `MessageCorrelationProcess.bpmn`
Java Code: `MyRestController`, `MySoapEndpoint` (SOAP version)


## Other guides
* Start process via SOAP web service generated with CXF: https://github.com/camunda/camunda-bpm-examples/tree/master/startevent/soap-cxf-server-start
* Best practices for the use of the start event: https://docs.camunda.io/docs/components/best-practices/development/routing-events-to-processes/
* Best practices for starting a process programmatically from a received message: https://docs.camunda.io/docs/components/best-practices/development/routing-events-to-processes/#camunda-platform-7