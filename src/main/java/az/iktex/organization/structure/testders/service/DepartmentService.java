
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.mapper.DepartmentMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    // CREATE
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {

        // Eyni adlı Department varsa, ERR-2201 qaytar
        if (departmentRepository.existsByName(request.name())) {
            throw new AlreadyExistsException(
                    ErrorCode.DEPARTMENT_ALREADY_EXISTS
            );
        }

        Department department = departmentMapper.toEntity(request);

        Department saved = departmentRepository.save(department);

        return departmentMapper.toResponse(saved);
    }

    // GET BY ID
    public DepartmentResponse getById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.DEPARTMENT_NOT_FOUND,
                                id
                        )
                );

        return departmentMapper.toResponse(department);
    }

    // GET ALL
    public List<DepartmentResponse> getAll() {

        return departmentMapper.toResponseList(
                departmentRepository.findAll()
        );
    }

    // UPDATE
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.DEPARTMENT_NOT_FOUND,
                                id
                        )
                );

        // Başqa Department eyni addan istifadə edirsə, ERR-2202
        if (departmentRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BusinessException(
                    ErrorCode.DEPARTMENT_NAME_USED
            );
        }

        departmentMapper.updateEntity(request, department);

        return departmentMapper.toResponse(department);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.DEPARTMENT_NOT_FOUND,
                                id
                        )
                );

        // Department-in işçiləri varsa, ERR-2203 qaytar
        if (department.getEmployees() != null
                && !department.getEmployees().isEmpty()) {

            throw new BusinessException(
                    ErrorCode.DEPARTMENT_HAS_EMPLOYEES
            );
        }

        departmentRepository.delete(department);
    }
}
