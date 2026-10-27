package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, unique = true, length = 50)
 private String name;

 @Column(length = 100)
 private String location;

 @Column(length = 20)
 private String phone;

 @OneToMany(
         mappedBy = "department",
         cascade = CascadeType.ALL,
         orphanRemoval = true,
         fetch = FetchType.LAZY
 )
 private List<Employee> employees = new ArrayList<>();

 public void addEmployee(Employee employee) {
  employees.add(employee);
  employee.setDepartment(this);
 }

 public void removeEmployee(Employee employee) {
  employees.remove(employee);
  employee.setDepartment(null);
 }
}