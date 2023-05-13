package workshop.spring.security.client;


import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class);
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> registerObservationPredicate() {
        ObservationPredicate predicate = (name, context) ->
                !name.startsWith("spring.security.filterchain");
        return (registry) -> registry.observationConfig().observationPredicate(predicate);
    }
}