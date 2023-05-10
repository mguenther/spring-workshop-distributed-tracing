package workshop.spring.tracing.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "client")
public class ClientConfiguration {

    private String micrometerUrl;

    private String user;

    private String password;

    public String getMicrometerUrl() {
        return micrometerUrl;
    }

    public void setMicrometerUrl(String micrometerUrl) {
        this.micrometerUrl = micrometerUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
