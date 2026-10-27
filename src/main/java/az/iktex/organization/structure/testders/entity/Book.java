
package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Check(name = "chk_book_price", constraints = "price >= 0")
@Cacheable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Book {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, length = 150)
 private String title;

 @Column(nullable = false, precision = 8, scale = 2)
 private BigDecimal price;

 @Column(name = "page_count", nullable = false)
 private Integer pageCount;

 @OneToMany(
         mappedBy = "book",
         cascade = CascadeType.ALL,
         orphanRemoval = true,
         fetch = FetchType.LAZY
 )
 @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 private List<AuthorBook> authorBooks = new ArrayList<>();
}
