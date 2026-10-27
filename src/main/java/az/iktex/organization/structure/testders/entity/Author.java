
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
@Table(name = "authors")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Author {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, length = 50)
 private String name;

 @Column(nullable = false, length = 50)
 private String surname;

 @Column(length = 50)
 private String country;

 @OneToMany(
         mappedBy = "author",
         cascade = CascadeType.ALL,
         orphanRemoval = true,
         fetch = FetchType.LAZY
 )
 @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 private List<AuthorBook> authorBooks = new ArrayList<>();

 public void addBook(Book book) {
  AuthorBook ab = new AuthorBook();
  ab.setAuthor(this);
  ab.setBook(book);

  authorBooks.add(ab);
  book.getAuthorBooks().add(ab);
 }
}
