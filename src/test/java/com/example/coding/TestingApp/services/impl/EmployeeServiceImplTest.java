package com.example.coding.TestingApp.services.impl;

import com.example.coding.TestingApp.TestContainerConfiguration;
import com.example.coding.TestingApp.dto.EmployeeDTO;
import com.example.coding.TestingApp.entities.Employee;
import com.example.coding.TestingApp.exception.ResourceNotFoundException;
import com.example.coding.TestingApp.repositories.EmployeeRepository;
import com.example.coding.TestingApp.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // not using in memory database if Any using in memeory databse
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private EmployeeDTO mockEmployeeDTO;

    @BeforeEach
    void setUp(){
        mockEmployee = Employee.builder()
                .id(1L)
                .email("shiv@gmail.com")
                .name("shiv")
                .salary(1000L)
                .build();
        mockEmployeeDTO = modelMapper.map(mockEmployee,EmployeeDTO.class);
    }

    Employee mockEmployee;
    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_thenReturnEmployeeDto(){
        //assign
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee)); // stubbing
        // Act

        EmployeeDTO employeeDTO = employeeService.getEmployeeById(id);

        // Asset
        assertThat(employeeDTO.getId()).isEqualTo(id);
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository,only()).findById(id);

    }

    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException(){
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");
        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenEmployeeIsValid_thenCreateNewEmployee(){
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeDTO employeeDTO = employeeService.createNewEmployee(mockEmployeeDTO);

        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployeeDTO.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee employeeCapture = employeeArgumentCaptor.getValue();
        assertThat(employeeCapture.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenTrowException(){
        when(employeeRepository.findByEmail(mockEmployeeDTO.getEmail())).thenReturn(List.of(mockEmployee));
        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployee.getEmail());
        verify(employeeRepository).findByEmail(mockEmployeeDTO.getEmail());
        verify(employeeRepository,never()).save(any());

    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //assert and act
        assertThatThrownBy(() -> employeeService.updateEmployee(1L,mockEmployeeDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");
        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingUpdateEmail_thenThrowException(){
        when(employeeRepository.findById(mockEmployeeDTO.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDTO.setName("deepak");
        mockEmployeeDTO.setEmail("deepak@gmail.com");
        assertThatThrownBy(()->employeeService.updateEmployee(mockEmployeeDTO.getId(),mockEmployeeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDTO.getId());
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){
        // arrange
        when(employeeRepository.findById(mockEmployeeDTO.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDTO.setName("Patu");
        mockEmployeeDTO.setSalary(2000L);

        Employee newEmployee = modelMapper.map(mockEmployeeDTO,Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);
        //act
        EmployeeDTO updateEmployeeDto = employeeService.updateEmployee(mockEmployeeDTO.getId(),mockEmployeeDTO);
        assertThat(updateEmployeeDto).isEqualTo(mockEmployeeDTO);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: "+1L);
        verify(employeeRepository).existsById(1L);
        verify(employeeRepository,never()).deleteById(1L);
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee(){
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() ->employeeService.deleteEmployee(1L))
                .doesNotThrowAnyException();
        verify(employeeRepository).deleteById(1L);
    }
}