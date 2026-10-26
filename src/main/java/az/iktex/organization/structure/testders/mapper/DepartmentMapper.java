
package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = EmployeeMapper.class
)
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Department toEntity(DepartmentRequest request);

    DepartmentResponse toResponse(Department department);

    List<DepartmentResponse> toResponseList(
            List<Department> departments
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntity(
            DepartmentRequest request,
            @MappingTarget Department department
    );
}
