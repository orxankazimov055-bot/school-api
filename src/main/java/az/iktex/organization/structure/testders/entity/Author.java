package az.iktex.organization.structure.testders.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity @Table(name="authors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Author {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=50) private String name;
 @Column(nullable=false,length=50) private String surname;
 @Column(length=50) private String country;
 @OneToMany(mappedBy="author",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.LAZY) private List<AuthorBook> authorBooks=new ArrayList<>();
 public void addBook(Book book){ AuthorBook ab=new AuthorBook();ab.setAuthor(this);ab.setBook(book);authorBooks.add(ab);book.getAuthorBooks().add(ab);}
}
