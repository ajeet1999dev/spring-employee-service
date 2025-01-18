package com.example.coding.TestingApp.repositories;

import com.example.coding.TestingApp.TestContainerConfiguration;
import com.example.coding.TestingApp.entities.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestContainerConfiguration.class)
@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {

        System.out.println("we are in testing mode");
        employee = Employee.builder()
//                .id(1L)
                .name("Ajeet")
                .email("ajeet@gmail.com")
                .salary(100L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployeeList() {

        employeeRepository.save(employee);

        List<Employee> employeeList =employeeRepository.findByEmail(employee.getEmail());

        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList).isNotEmpty();
        Assertions.assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());

    }
    @Test
    void testFindByEmail_whenEmailIsNotValid_thenReturnEmptyEmployeeList(){

        String email = "shiv@gmail.com";
        List<Employee> employeeList =employeeRepository.findByEmail(email);
        Assertions.assertThat(employeeList).isNotNull();
        Assertions.assertThat(employeeList).isEmpty();

    }
}