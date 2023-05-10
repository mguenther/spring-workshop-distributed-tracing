package workshop.spring.tracing.task;


import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class UserRetrievalTask {

    private static final Logger log = LoggerFactory.getLogger(UserRetrievalTask.class);

    private final Tracer tracer;
    private final RestTemplate template;

    @Autowired
    public UserRetrievalTask(Tracer tracer, RestTemplate template) {
        this.tracer = tracer;
        this.template = template;
    }

    @Scheduled(fixedDelay = 10_000, initialDelay = 5_000)
    public void retrieveUser() {
        log.info("Retrieve User task triggered.");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        tracer.currentSpan().annotate("Retrieving user object");
        var user = template.exchange("/user", HttpMethod.GET, entity, UserPermission.class);

        tracer.currentSpan().annotate("Authorities " + user.getBody().getAuthorityString());
        tracer.currentSpan().annotate("Username " + user.getBody().name());
    }

}

record UserPermission(String name, List<String> authorities) {
    public String getAuthorityString() {
        return String.join(", ", authorities);
    }
}