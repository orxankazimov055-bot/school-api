package az.iktex.organization.structure.testders.entity;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.entity.converter.GenderConverter;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_users_email", columnNames="email"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=50) private String name;
 @Column(nullable=false,length=50) private String surname;
 @Column(nullable=false,unique=true,length=100) private String email;
 @Convert(converter=GenderConverter.class) @Column(name="gender_id",nullable=false) private Gender gender;
 @OneToOne(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY) private Passport passport;
 public void setPassport(Passport passport) {
   if(this.passport != null && this.passport != passport) this.passport.setUser(null);
   this.passport=passport;
   if(passport != null && passport.getUser()!=this) passport.setUser(this);
 }
}
