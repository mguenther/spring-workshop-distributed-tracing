package workshop.spring.security.client;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/oauth2/**", "/login").permitAll()
                .anyRequest().authenticated()
        ).oauth2Login(oauth2 ->
                oauth2.loginPage("/login")
                        .userInfoEndpoint()
                        .userService(new DefaultOAuth2UserService())
                        .and()
                        .successHandler((request, response, authentication) -> response.sendRedirect("/"))
        );

        return http.build();
    }
}
