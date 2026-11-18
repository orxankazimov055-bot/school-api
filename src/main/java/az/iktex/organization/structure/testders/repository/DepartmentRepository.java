
package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Eyni adlı Department mövcuddurmu?
    boolean existsByName(String name);

    // Bu ID-dən başqa Department eyni addan istifadə edirmi?
    boolean existsByNameAndIdNot(String name, Long id);
}
