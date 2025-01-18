package com.example.coding.TestingApp.controller;

import com.example.coding.TestingApp.TestContainerConfiguration;
import com.example.coding.TestingApp.dto.EmployeeDTO;
import com.example.coding.TestingApp.entities.Employee;
import com.example.coding.TestingApp.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;



@AutoConfigureWebTestClient(timeout = "100000")
@Import(TestContainerConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTestIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;
    private EmployeeDTO testEmployeeDto;

    @BeforeEach
    void setUp(){
        testEmployee = Employee.builder()
                .id(1L)
                .email("shiv@gmail.com")
                .name("shiv")
                .salary(1000L)
                .build();
        testEmployeeDto = EmployeeDTO.builder()
                .id(1L)
                .email("shiv@gmail.com")
                .name("shiv")
                .salary(1000L)
                .build();

        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success(){

        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedEmployee.getId())
                .jsonPath("$.email").isEqualTo(savedEmployee.getEmail());

//                .value(employeeDTO -> {
//                    Assertions.assertThat(employeeDTO.getEmail()).isEqualTo(savedEmployee.getEmail());
//                });

    }

    @Test
    void testGetEmployeeById_Failure(){
        webTestClient.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExits_thenThrowException(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee(){
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        webTestClient.put()
                .uri("/employees/1323")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenUpdateThrowException(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Ramdom name");
        testEmployeeDto.setEmail("random@gmail.com");
        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Ramdom name");
        testEmployeeDto.setSalary(250L);
        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDTO.class)
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void testDeleteEmployee_whenEmployeeExits_thenDeleteEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.delete()
                .uri("/employees/{id}",savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }
}