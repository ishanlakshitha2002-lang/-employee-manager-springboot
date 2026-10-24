package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(Model model) {
        model.addAttribute("employees", service.getAllEmployees());
        model.addAttribute("employee", new Employee());
        return "employees";
    }

    @PostMapping("/employees")
    public String createEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("employees", service.getAllEmployees());
            return "employees";
        }

        if (service.emailExists(employee.getEmail())) {
            result.rejectValue(
                "email", "duplicate",
                "This email is already registered"
            );
            model.addAttribute("employees", service.getAllEmployees());
            return "employees";
        }

        service.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("success", "Employee registered successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String editEmployee(
            @PathVariable Long id, Model model) {

        Employee employee = service.getEmployeeById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid employee ID"));

        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/employees/{id}/update")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Employee existing = service.getEmployeeById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid employee ID"));

        if (result.hasErrors()) {
            employee.setId(id);
            return "edit-employee";
        }

        if (service.emailExists(employee.getEmail())
                && !employee.getEmail().equals(existing.getEmail())) {
            result.rejectValue(
                "email", "duplicate",
                "This email is already registered"
            );
            employee.setId(id);
            return "edit-employee";
        }

        employee.setId(id);
        service.saveEmployee(employee);
        redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}
