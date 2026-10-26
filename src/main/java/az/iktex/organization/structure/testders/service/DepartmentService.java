
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.mapper.DepartmentMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;

import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);

        Department saved = departmentRepository.save(department);

        return departmentMapper.toResponse(saved);
    }

    public DepartmentResponse getById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Department tapilmadi: " + id
                        )
                );

        return departmentMapper.toResponse(department);
    }

    public List<DepartmentResponse> getAll() {

        return departmentMapper.toResponseList(
                departmentRepository.findAll()
        );
    }

    @Transactional
    public DepartmentResponse update(
            Long id,
            DepartmentRequest request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Department tapilmadi: " + id
                        )
                );

        departmentMapper.updateEntity(request, department);

        return departmentMapper.toResponse(department);
    }

    @Transactional
    public void delete(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Department tapilmadi: " + id
                        )
                );

        departmentRepository.delete(department);
    }
}
