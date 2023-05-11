# Lab Assignment

You may have guessed it - it's your task to integrate Micrometer Observation into the systems.

## Task #1: Integrate Micrometer into the Client Application

At first, we want to enable our Client Application to use observations and send traces to Zipkin.

1. Add the missing dependencies to the `client-application` module. You can use the `micrometer-tracing` module as a template.
2. Configure our Spring Application for Micrometer and Zipkin and make sure that the Client Application sends traces to Zipkin after startup - Reminder: Zipkin listens on http://localhost:9411
3. Take a look at the traces - can you explain every single one of them? Is everything present we expected?

## Task #2: Integrate Micrometer into the Resource Server

To get any meaningful tracing-context we also have to integrate Micrometer with the resource server and then make sure that the context propagation works as intended.

1. Repeat Task 1.1 and 1.2 for the resource server. 
2. Trigger a request via the client application and make sure that you can see additional spans in Zipkin. 
3. The result is not what we expected, is it? Take a few minutes to compare the HttpHeaders between the `micrometer-tracing` Endpoint and the `resource-server` Endpoint. What are we missing? Can you fix it?

## Task #3 Integrate the tracing into the Authorization Server  & Optimization


1. Same as before: integrate the necessary dependencies, add the configuration and make sure that the authorization server writes traces to Zipkin. Use the provided HTTP requests if you did not integrate the authorization server yet.
2. We - again - seem to have an issue with a missing correlation ID from the Client Application. Fix this.
3. We now have a beautiful, detailed latency tree. Maybe a little **too** detailed. We can and should filter some stuff we're not necessarily interested in. Micrometer Observation offers an `ObservationRegistryCustomizer`. Use this to filter out some observations you think are not necessary.

```java
@Bean
ObservationRegistryCustomizer<ObservationRegistry> registerObservationPredicate() {
    ObservationPredicate predicate = (...); // TODO
    return (registry) -> registry.observationConfig().observationPredicate(predicate);
}
```

## Task #4 Custom Observations

Up until now we only ensured that the autowired observations are reported to Zipkin. For the final task of this Lab we want to add our own Observations and customize them. The resource server with its HTTP facades and DB integration is the best target for that.

1. Start a new Observation whenever the role resolution is triggered in `EmployeeController.employeeToRoleBasedView`. To start a new Observation you need the `ObservationRegisty`, which you can Inject into the Controller. To start a new Observation and observe some logic use:
```java
var observation = Observation.createNotStarted(name, registry);
observation.observe(() -> 
    { 
        // runnable 
    });
```
After you added the Observation make sure that it is reported to Zipkin. You can use the client application or the resources.http file to trigger the necessary calls.

2. Now that we have an observation we can add Annotations (Events in Micrometer Observation) and tags (low/high cardinality values in Micrometer Observation) to this observation. Make sure that every Observation in `EmployeeController.employeeToRoleBasedView`
   1. has an annotation before the Mapper reads and returns the value.
   2. has a tag with all authorities of the current user (comma separated).  

## That's it! You've done great!

You have completed all assignments. If you have any further questions or need clarification, please don't hesitate to reach out to us. We're here to help.