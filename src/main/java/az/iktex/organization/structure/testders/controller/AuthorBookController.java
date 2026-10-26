
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.response.AuthorBookResponse;
import az.iktex.organization.structure.testders.service.AuthorBookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author-books")
@RequiredArgsConstructor
public class AuthorBookController {

    private final AuthorBookService authorBookService;

    @PostMapping("/authors/{authorId}/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorBookResponse addBookToAuthor(
            @PathVariable Long authorId,
            @PathVariable Long bookId) {

        return authorBookService.addBookToAuthor(authorId, bookId);
    }

    @GetMapping
    public List<AuthorBookResponse> getAll() {

        return authorBookService.getAll();
    }

    @DeleteMapping("/authors/{authorId}/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookFromAuthor(
            @PathVariable Long authorId,
            @PathVariable Long bookId) {

        authorBookService.removeBookFromAuthor(authorId, bookId);
    }
}
