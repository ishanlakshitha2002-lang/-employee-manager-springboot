package com.example.employee_manager.service;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.repository.EmployeeRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return repository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public List<Employee> searchEmployees(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return repository.findByFullNameContainingIgnoreCase(keyword.trim());
        }
        return repository.findAll();
    }
}
