package workshop.spring.security.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class TokenInterceptor implements ClientHttpRequestInterceptor {

    private final EnvironmentConfiguration config;

    private final ObservationRegistry registry;

    public TokenInterceptor(EnvironmentConfiguration config, ObservationRegistry registry) {
        this.config = config;
        this.registry = registry;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization", "Bearer " + retrieveToken());
        return execution.execute(request, body);
    }

    private String retrieveToken() {
        var restTemplate = new RestTemplate();
        restTemplate.setObservationRegistry(registry);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&client_id=employee-client&client_secret=EquaiLahp7ko&scope=MANAGEMENT";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<AuthorizationServerTokenResponse> response =
                restTemplate.exchange(config.getTokenUrl(),
                        HttpMethod.POST,
                        entity,
                        AuthorizationServerTokenResponse.class);

        return response.getBody().accessToken();
    }

    record AuthorizationServerTokenResponse(String accessToken) {

        @JsonCreator
            AuthorizationServerTokenResponse(@JsonProperty("access_token") String accessToken) {
                this.accessToken = accessToken;
            }
        }
}
