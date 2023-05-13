package workshop.spring.security.resources;


import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerApplication.class);
    }

    @Bean
    public OpenAPI openApiDocumentation() {
        return new OpenAPI();
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> registerObservationPredicate() {
        ObservationPredicate predicate = (name, context) ->
                !name.startsWith("spring.security.filterchain");
        return (registry) -> registry.observationConfig().observationPredicate(predicate);
    }
}