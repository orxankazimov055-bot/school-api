
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.exception.InvalidRequestException;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.mapper.EmployeeMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    // CREATE
    @Transactional
    public EmployeeResponse create(
            Long departmentId,
            EmployeeRequest request) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.DEPARTMENT_NOT_FOUND,
                                departmentId
                        )
                );

        validateSalary(request.salary());

        // Eyni Department daxilində eyni adlı işçi varsa
        if (employeeRepository.existsByDepartmentIdAndNameAndSurname(
                departmentId,
                request.name(),
                request.surname()
        )) {
            throw new AlreadyExistsException(
                    ErrorCode.EMPLOYEE_ALREADY_EXISTS
            );
        }

        Employee employee = employeeMapper.toEntity(request);
        employee.setDepartment(department);

        Employee saved = employeeRepository.save(employee);

        return employeeMapper.toResponse(saved);
    }

    // GET BY ID
    public EmployeeResponse getById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                id
                        )
                );

        return employeeMapper.toResponse(employee);
    }

    // GET ALL
    public List<EmployeeResponse> getAll() {

        return employeeMapper.toResponseList(
                employeeRepository.findAll()
        );
    }

    // UPDATE
    @Transactional
    public EmployeeResponse update(
            Long id,
            EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                id
                        )
                );

        validateSalary(request.salary());

        employeeMapper.updateEntity(request, employee);

        return employeeMapper.toResponse(employee);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.EMPLOYEE_NOT_FOUND,
                                id
                        )
                );

        employeeRepository.delete(employee);
    }

    // SALARY VALIDATION
    private void validateSalary(BigDecimal salary) {

        if (salary == null ||
                salary.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidRequestException(
                    ErrorCode.EMPLOYEE_SALARY_RANGE
            );
        }
    }
}
