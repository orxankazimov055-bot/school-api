
package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Author;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByNameAndSurname(String name, String surname);

    @QueryHints(
            @QueryHint(
                    name = "org.hibernate.cacheable",
                    value = "true"
            )
    )
    List<Author> findAllByCountry(String country);

    @Query("select a from Author a where a.authorBooks is empty")
    List<Author> findWithoutBooks();

    @Query("""
            select distinct a
            from Author a
            left join fetch a.authorBooks ab
            left join fetch ab.book
            where a.id = :id
            """)
    Optional<Author> findDetailById(@Param("id") Long id);
}
