
package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Course {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, unique = true, length = 100)
 private String title;

 @Column(nullable = false)
 private Integer credit;

 @Column(nullable = false, length = 50)
 private String teacher;

 @Column(name = "capacity", nullable = false)
 private Integer capacity = 30;

 @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
 private Set<Student> students = new HashSet<>();
}
