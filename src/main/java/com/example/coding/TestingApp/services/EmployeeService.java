package com.example.coding.TestingApp.services;

import com.example.coding.TestingApp.dto.EmployeeDTO;

public interface EmployeeService {

    EmployeeDTO getEmployeeById(Long employeeId);
    EmployeeDTO createNewEmployee(EmployeeDTO inputEmployeeDto);
    EmployeeDTO updateEmployee(Long employeeId, EmployeeDTO employeeDTO);
    void deleteEmployee(Long employeeId);
}
