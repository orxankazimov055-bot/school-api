package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.BookService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // Kitab yaratmaq
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(
            @RequestBody BookRequest request) {

        return bookService.create(request);
    }

    // Butun kitablari gostermek
    @GetMapping
    public List<BookResponse> getAll() {

        return bookService.getAll();
    }

    // ID ile kitab tapmaq
    @GetMapping("/{id}")
    public BookResponse getById(
            @PathVariable Long id) {

        return bookService.getById(id);
    }

    // Kitab silmek
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        bookService.delete(id);
    }
}