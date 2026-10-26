package az.iktex.organization.structure.testders.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity @Table(name="courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Course {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=100) private String title;
 @Column(nullable=false) private Integer credit;
 @Column(nullable=false,length=50) private String teacher;
 @ManyToMany(mappedBy="courses",fetch=FetchType.LAZY) private Set<Student> students=new HashSet<>();
}
