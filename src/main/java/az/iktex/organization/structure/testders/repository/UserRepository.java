package az.iktex.organization.structure.testders.repository;
import az.iktex.organization.structure.testders.entity.User; import az.iktex.organization.structure.testders.enums.Gender; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.util.*;
public interface UserRepository extends JpaRepository<User,Long> {
 boolean existsByEmail(String email); boolean existsByEmailAndIdNot(String email,Long id); List<User> findAllByGender(Gender gender); List<User> findAllByPassportIsNull(); Optional<User> findByEmail(String email);
 @Query("select u from User u left join fetch u.passport where u.id=:id") Optional<User> findDetailById(@Param("id") Long id);
 @Query("select u from User u left join fetch u.passport") List<User> findAllWithPassport();
}