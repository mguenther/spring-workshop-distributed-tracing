package workshop.spring.tracing.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBean {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateBean.class);

    private final ClientConfiguration config;

    public RestTemplateBean(ClientConfiguration config) {
        this.config = config;
    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("Initializing restTemplate with micrometer url '{}' and user '{}'",
                config.getMicrometerUrl(), config.getUser());
        return new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(config.getMicrometerUrl()))
                .basicAuthentication(config.getUser(), config.getPassword())
                .build();
    }
}
