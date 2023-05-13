package workshop.spring.security.resources;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import workshop.spring.security.resources.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz ->
                        authz.requestMatchers("/swagger-ui.html", "/swagger-ui/*", "/v3/**", "/whoami").permitAll()
                                .requestMatchers("/**").authenticated()
                )
                .httpBasic()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}user")
                .authorities("INTERNAL")
                .build();

        UserDetails accounting = User.builder()
                .username("accounting")
                .password("{noop}accounting")
                .authorities("ACCOUNTING")
                .build();

        UserDetails management = User.builder()
                .username("management")
                .password("{noop}management")
                .authorities("MANAGEMENT")
                .build();

        return new InMemoryUserDetailsManager(user, accounting, management);
    }
}
