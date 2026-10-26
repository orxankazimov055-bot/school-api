package az.iktex.organization.structure.testders.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity @Table(name="students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=50) private String name;
 @Column(nullable=false,length=50) private String surname;
 @Column(nullable=false) private Integer age;
 @ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE},fetch=FetchType.LAZY)
 @JoinTable(name="student_course",
   joinColumns=@JoinColumn(name="student_id",foreignKey=@ForeignKey(name="fk_sc_student")),
   inverseJoinColumns=@JoinColumn(name="course_id",foreignKey=@ForeignKey(name="fk_sc_course")),
   uniqueConstraints=@UniqueConstraint(name="uk_student_course",columnNames={"student_id","course_id"}))
 private Set<Course> courses=new HashSet<>();
 public void addCourse(Course c){courses.add(c);c.getStudents().add(this);}
 public void removeCourse(Course c){courses.remove(c);c.getStudents().remove(this);}
 // REMOVE cascade verilmir: tələbə silinəndə kurs yox, yalnız student_course əlaqəsi silinməlidir.
}
