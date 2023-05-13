package workshop.spring.security.resources.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import workshop.spring.security.resources.data.Employee;
import workshop.spring.security.resources.data.EmployeeDataRepository;
import workshop.spring.security.resources.data.PerformanceRating;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerV2MvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Employee Endpoint disallows anonymous access")
    void employeeEndpointDisallowsAnonymousAccess() throws Exception {
        mockMvc.perform(get("/employee"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("StatisticsEndpoint only returns data with authority MANAGEMENT")
    void statisticsEndpointOnlyAllowsManagementRole() throws Exception {
        mockMvc.perform(get("/employee/statistics"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/employee/statistics")
                        .with(user("doesnt").password("matter").authorities(new SimpleGrantedAuthority("INTERNAL"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/employee/statistics")
                        .with(user("doesnt").password("matter").authorities(new SimpleGrantedAuthority("ACCOUNTING"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/employee/statistics")
                        .with(user("very").password("important").authorities(new SimpleGrantedAuthority("MANAGEMENT"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Employee Endpoint returns objects for authorized user")
    @WithMockUser(username = "whatever", authorities = {"INTERNAL"})
    void employeeEndpointReturnsObjectsForAuthorizedUser() throws Exception {
        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class MvcTestConfiguration {

        @Bean
        EmployeeDataRepository mockRepo() {
            var employee = new Employee();
            employee.setPerformanceRating(PerformanceRating.GOOD);

            var repoMock = Mockito.mock(EmployeeDataRepository.class);
            when(repoMock.findAll()).thenReturn(List.of(employee));
            return repoMock;
        }
    }
}
