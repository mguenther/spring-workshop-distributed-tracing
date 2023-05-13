package workshop.spring.security.resources.controller;


import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import workshop.spring.security.resources.data.EmployeeDataRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class EmployeeControllerV2JpaTest {

    @Autowired
    private EmployeeDataRepository repo;
    private EmployeeController controller;
    private final static Authentication INTERNAL = new UsernamePasswordAuthenticationToken(null, null, List.of(new SimpleGrantedAuthority("INTERNAL")));
    private final static Authentication ACCOUNTING = new UsernamePasswordAuthenticationToken(null, null, List.of(new SimpleGrantedAuthority("ACCOUNTING")));
    private final static Authentication MANAGEMENT = new UsernamePasswordAuthenticationToken(null, null, List.of(new SimpleGrantedAuthority("MANAGEMENT")));

    @BeforeAll
    void bootstrap() {
        controller = new EmployeeController(repo);
    }

    @Test
    @DisplayName("Asserts that the repository contains 25 Objects after bootstrapping")
    void repoContains25ObjectsAfterBootstrapping() {
        assertThat(repo.findAll()).hasSize(25);
    }

    @Test
    @DisplayName("Employee Endpoint returns no salary/performance rating for internal")
    void employeeEndpointReturnsReducedDetailsForInternalPrivilege() {
        var dtos = controller.getEmployees(INTERNAL);
        assertThat(dtos).allMatch(it -> it.getSalary() == null);
        assertThat(dtos).allMatch(it -> it.getPerformanceRating() == null);
    }

    @Test
    @DisplayName("Employee Endpoint returns reduced details for accounting privilege")
    void employeeEndpointReturnsReducedDetailsForAccountingPrivilege() {
        var dtos = controller.getEmployees(ACCOUNTING);
        assertThat(dtos).allMatch(it -> it.getPerformanceRating() == null);
    }

    @Test
    @DisplayName("Employee Endpoint returns all details for management privilege")
    void employeeEndpointReturnsReducedDetailsForManagementPrivilege() {
        var dtos = controller.getEmployees(MANAGEMENT);
        assertThat(dtos).allMatch(it -> it.getPerformanceRating() != null);
        assertThat(dtos).allMatch(it -> it.getSalary() != null);
    }

    @Test
    @DisplayName("Employee Endpoint returns all details for management privilege")
    void employeeStatisticsEndpointGivesCorrectValues() {
        var stats = controller.getEmployeeStatistics();
        AssertionsForClassTypes.assertThat(stats).isNotNull();
        AssertionsForClassTypes.assertThat(stats.getNumberOfEmployees()).isEqualTo(25);
        AssertionsForClassTypes.assertThat(stats.getHighestPaidEmployee()).isEqualTo("Robert Downey Jr.");
    }
}
