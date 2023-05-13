package workshop.spring.security.resources.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import workshop.spring.security.resources.data.EmployeeDto;
import workshop.spring.security.resources.data.EmployeeStatisticsDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerV1ImprovedTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Employee Endpoint disallows anonymous access")
    void employeeEndpointDisallowsAnonymousAccess() throws Exception {
        mockMvc.perform(get("/employee"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Employee Endpoint returns all objects for authorized user")
    void employeeEndpointReturnsObjectsForAuthorizedUser() throws Exception {
        mockMvc.perform(get("/employee")
                        .with(user("doesnt")
                                .password("matter")
                                .authorities(new SimpleGrantedAuthority("ACCOUNTING"))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['id']", hasSize(25)));
    }

    @Test
    @DisplayName("Employee Endpoint returns reduced details for internal privilege")
    @WithMockUser(username = "whatever", authorities = {"INTERNAL"})
    void employeeEndpointReturnsReducedDetailsForInternalPrivilege() throws Exception {
        var stringResponse = mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var dtos = mapper.readValue(stringResponse, new TypeReference<List<EmployeeDto>>() {
        });
        assertThat(dtos).allMatch(it -> it.getSalary() == null);
        assertThat(dtos).allMatch(it -> it.getPerformanceRating() == null);
    }

    @Test
    @DisplayName("StatisticsEndpoint only returns data with authority MANAGEMENT")
    void statisticsEndpointOnlyReturnsDataForManagement() throws Exception {
        mockMvc.perform(get("/employee/statistics")
                .with(user("notso")
                        .password("important")
                        .authorities(new SimpleGrantedAuthority("ACCOUNTING")))
        ).andExpect(status().isForbidden());

        var stringResponse = mockMvc.perform(get("/employee/statistics")
                        .with(user("very")
                                .password("important")
                                .authorities(new SimpleGrantedAuthority("MANAGEMENT"))
                        )
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var stats = mapper.readValue(stringResponse, EmployeeStatisticsDto.class);
        assertThat(stats).isNotNull();
        assertThat(stats.getNumberOfEmployees()).isEqualTo(25);
        assertThat(stats.getHighestPaidEmployee()).isEqualTo("Robert Downey Jr.");
    }
}
