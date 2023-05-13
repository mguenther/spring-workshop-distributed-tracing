package workshop.spring.server;

import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class AuthorizationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class);
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> registerObservationPredicate() {
        ObservationPredicate predicate = (name, context) ->
                !name.startsWith("spring.security.filterchain");
        return (registry) -> registry.observationConfig().observationPredicate(predicate);
    }
}