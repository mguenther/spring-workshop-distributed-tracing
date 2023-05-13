package workshop.spring.security.resources.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import workshop.spring.security.resources.data.EmployeeDto;
import workshop.spring.security.resources.data.EmployeeStatisticsDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerV1Test {

    @LocalServerPort
    int randomServerPort;

    private RestTemplate template;

    @BeforeEach
    void setupClient() {
        template = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + randomServerPort + "/employee"))
                .build();
    }

    @Test
    @DisplayName("Employee Endpoint disallows anonymous access")
    void employeeEndpointDisallowsAnonymousAccess() {
        assertThatThrownBy(() -> template.getForEntity("", EmployeeDto.class))
                .isInstanceOf(HttpClientErrorException.Unauthorized.class);
    }

    @Test
    @DisplayName("Employee Endpoint returns all objects for authorized user")
    void employeeEndpointReturnsObjectsForAuthorizedUser() {
        var response = getEmployeesWithCredentials("accounting", "accounting");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(25);
    }

    @Test
    @DisplayName("Employee Endpoint returns reduced details for internal privilege")
    void employeeEndpointReturnsReducedDetailsForInternalPrivilege() {
        var response = getEmployeesWithCredentials("user", "user");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(25);

        var dtos = response.getBody();
        assertThat(dtos).allMatch(it -> it.getSalary() == null);
        assertThat(dtos).allMatch(it -> it.getPerformanceRating() == null);
    }

    @Test
    @DisplayName("StatisticsEndpoint only returns data with authority MANAGEMENT")
    void statisticsEndpointOnlyReturnsDataForManagement() {
        assertThatThrownBy(() -> template.getForEntity("/statistics", EmployeeStatisticsDto.class))
                .isInstanceOf(HttpClientErrorException.Unauthorized.class);

        assertThatThrownBy(() -> getStatisticsWithCredentials("user", "user"))
                .isInstanceOf(HttpClientErrorException.Forbidden.class);

        var response = getStatisticsWithCredentials("management", "management");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var stats = response.getBody();
        assertThat(stats).isNotNull();
        assertThat(stats.getNumberOfEmployees()).isEqualTo(25);
        assertThat(stats.getHighestPaidEmployee()).isEqualTo("Robert Downey Jr.");
    }

    private ResponseEntity<EmployeeDto[]> getEmployeesWithCredentials(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return template.exchange("", HttpMethod.GET, entity, EmployeeDto[].class);
    }

    private ResponseEntity<EmployeeStatisticsDto> getStatisticsWithCredentials(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return template.exchange("/statistics", HttpMethod.GET, entity, EmployeeStatisticsDto.class);
    }
}
