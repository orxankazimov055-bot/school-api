
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.mapper.EmployeeMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse create(
            Long departmentId,
            EmployeeRequest request) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Department tapilmadi: " + departmentId
                        )
                );

        Employee employee = employeeMapper.toEntity(request);

        employee.setDepartment(department);

        Employee saved = employeeRepository.save(employee);

        return employeeMapper.toResponse(saved);
    }

    public EmployeeResponse getById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Employee tapilmadi: " + id
                        )
                );

        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> getAll() {

        return employeeMapper.toResponseList(
                employeeRepository.findAll()
        );
    }

    @Transactional
    public EmployeeResponse update(
            Long id,
            EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Employee tapilmadi: " + id
                        )
                );

        employeeMapper.updateEntity(request, employee);

        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Employee tapilmadi: " + id
                        )
                );

        employeeRepository.delete(employee);
    }
}
