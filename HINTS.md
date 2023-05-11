# Hints

**Spoiler Alert**

We encourage you to work on the assignment yourself or together with your peers. However, situations may present themselves to you where you're stuck on a specific assignment. Thus, this document contains a couple of hints that ought to guide you through a specific task of the lab assignment.

In any case, don't hesitate to talk to us if you're stuck on a given problem!

## Task #1: Integrate Micrometer into the Client Application

At first, we want to enable our Client Application to use observations and send traces to Zipkin.

1. 
```xml
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-observation</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>io.zipkin.reporter2</groupId>
            <artifactId>zipkin-reporter-brave</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

2. 
```yaml
spring.application.name: Resource Server
logging.pattern.level: "%5p [${spring.zipkin.service.name:${spring.application.name:}},%X{traceId:-},%X{spanId:-}]"

management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: ${ZIPKIN_API_ENDPOINT:http://127.0.0.1:9411/api/v2/spans}
```

3. The login procedure generates several traces that can't be correlated because they're new requests from the browser after redirects. We're also missing the Web-Calls from the Client Application to the Resource Server.

## Task #2: Integrate Micrometer into the Resource Server

To get any meaningful tracing-context we also have to integrate Micrometer with the resource server and then make sure that the context propagation works as intended.

1. See above, you just have to adapt the application name
3. If you compare the HTTP Headers you will see that the parent ID is missing. The `micrometer-tracing` application receives an additional `X-B3(...)` header we don't see in the resource server -> it seems like our `RestTemplate` is not wired to use Micrometer yet. 
The easiest way to fix that is to add the appropriate customizer to the `@Bean` definition. It will have to look like this:
```java
public RestTemplate restTemplate(TokenInterceptor tokenInterceptor, ObservationRegistry registry) {
    return new RestTemplateBuilder()
        .uriTemplateHandler(new DefaultUriBuilderFactory(config.getResourceUrl()))
        .additionalCustomizers(new ObservationRestTemplateCustomizer(registry, new DefaultClientRequestObservationConvention()))
        .additionalInterceptors(tokenInterceptor)
        .build();
}
```

## Task #3 Integrate the tracing into the Authorization Server  & Optimization


1. Same as before.
2. We use a second `RestTemplate` in the `TokenInterceptor` - we need to inject the `ObservationRegistry` in the `TokenInterceptor` and either:
    1. register it on the `RestTemplate` directly via `restTemplate.setObservationRegistry(registry);`
   2. refactor the code to use the `RestTemplateBuilder` and add the customizer same as in 2.3
3. Where is no right or wrong here - you could for example filter all spring-security related traces in the client application, as the filters are of little interest here:

```java
@Bean
ObservationRegistryCustomizer<ObservationRegistry> registerObservationPredicate() {
    ObservationPredicate predicate = (name, context) -> !name.startsWith("spring.security");
    return (registry) -> registry.observationConfig().observationPredicate(predicate);
}
```

## Task #4 Custom Observations


1. Inject the `ObservationRegistry` into the `EmployeeController`, then the most direct approach is to add the observation to the method like this
```java
var observation = Observation.createNotStarted("employeeToRoleBased", registry);
    return observation.observe(() -> {
        try {
        var dto=EmployeeDto.fromEmployee(employee);
        (....)
    });
```

2. For the first part modify the code to look like this:
```java
 registry.getCurrentObservation().event(Observation.Event.of("Finished role resolution"));
 return mapper.readValue(serialized, EmployeeDto.class);
```

The tag can be added with this code anywhere within the observation-lambda:
```java
registry.getCurrentObservation().lowCardinalityKeyValue("role", 
        authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","))
        );
```

## That's it! You've done great!

You have completed all assignments. If you have any further questions or need clarification, please don't hesitate to reach out to us. We're here to help.