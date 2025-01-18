package com.example.coding.TestingApp.services.impl;

import com.example.coding.TestingApp.dto.EmployeeDTO;
import com.example.coding.TestingApp.entities.Employee;
import com.example.coding.TestingApp.exception.ResourceNotFoundException;
import com.example.coding.TestingApp.repositories.EmployeeRepository;
import com.example.coding.TestingApp.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeDTO getEmployeeById(Long employeeId) {

        log.info("fetching employee details with id: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new ResourceNotFoundException("Employee not found with id: "+employeeId);
                });
        log.info("Successfully fetched employee details with id: {}", employeeId);
        return modelMapper.map(employee,EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO createNewEmployee(EmployeeDTO inputEmployeeDto) {

        log.info("Creating new employee with email: {}", inputEmployeeDto.getEmail());
        List<Employee> existingEmployee = employeeRepository.findByEmail(inputEmployeeDto.getEmail());

        if(!existingEmployee.isEmpty()){
            log.info("Employee already exist with email: {}", inputEmployeeDto.getEmail());
            throw new RuntimeException("Employee already exists with email: "+inputEmployeeDto.getEmail());
        }
        Employee newEmployee = modelMapper.map(inputEmployeeDto,Employee.class);
        Employee saveEmployee = employeeRepository.save(newEmployee);
        log.info("successfully created new employee with id: {}", saveEmployee.getId());
        return modelMapper.map(saveEmployee,EmployeeDTO.class);
    }

    @Override
    public EmployeeDTO updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        log.info("Updating employee details with id: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", employeeId);
                    return new ResourceNotFoundException("Employee not found with id: "+employeeId);
                });

        if(!employee.getEmail().equals(employeeDTO.getEmail())){
            log.error("Attempted to update email for employee with id: {}", employeeId);
            throw new RuntimeException("The email of the employee cannot be updated");
        }

        modelMapper.map(employeeDTO,employee);
        employee.setId(employeeId);
        Employee saveEmployee = employeeRepository.save(employee);
        log.info("Successfully updated employee with id: {}", employeeId);
        return modelMapper.map(saveEmployee,EmployeeDTO.class);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        log.info("Deleting employee with id: {}", employeeId);
        boolean exists = employeeRepository.existsById(employeeId);
        if(!exists){
            log.error("Employee not found with id: {}", employeeId);
            throw  new ResourceNotFoundException("Employee not found with id: "+employeeId);
        }
        employeeRepository.deleteById(employeeId);
        log.info("Successfully deleted employee with id: {}", employeeId);
    }
}
