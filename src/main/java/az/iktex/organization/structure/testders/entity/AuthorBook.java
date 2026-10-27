
package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
        name = "author_book",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_author_book",
                columnNames = {"author_id", "book_id"}
        )
)
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AuthorBook {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(
         name = "author_id",
         nullable = false,
         foreignKey = @ForeignKey(name = "fk_ab_author")
 )
 private Author author;

 @ManyToOne(fetch = FetchType.LAZY, optional = false)
 @JoinColumn(
         name = "book_id",
         nullable = false,
         foreignKey = @ForeignKey(name = "fk_ab_book")
 )
 private Book book;
}
