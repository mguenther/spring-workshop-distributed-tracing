package workshop.spring.security.client;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.actuate.metrics.web.client.ObservationRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfiguration {

    private final EnvironmentConfiguration config;

    public WebClientConfiguration(EnvironmentConfiguration config) {
        this.config = config;
    }

    @Bean
    public RestTemplate restTemplate(TokenInterceptor tokenInterceptor,
                                     ObservationRegistry registry) {
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(config.getResourceUrl()))
                .additionalInterceptors(tokenInterceptor)
                .customizers(new ObservationRestTemplateCustomizer(registry,
                        new DefaultClientRequestObservationConvention()))
                .build();
    }
}
