
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.entity.AuthorBook;

import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.repository.BookRepository;
import az.iktex.organization.structure.testders.repository.AuthorBookRepository;

import az.iktex.organization.structure.testders.mapper.AuthorBookMapper;
import az.iktex.organization.structure.testders.response.AuthorBookResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorBookService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorBookRepository authorBookRepository;
    private final AuthorBookMapper authorBookMapper;

    @Transactional
    public AuthorBookResponse addBookToAuthor(
            Long authorId, Long bookId) {

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new RuntimeException("Author tapilmadi: " + authorId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Book tapilmadi: " + bookId));

        if (authorBookRepository.existsByAuthorIdAndBookId(authorId, bookId)) {
            throw new IllegalArgumentException(
                    "Bu muellif ve kitab artiq elaqelendirilib");
        }

        AuthorBook authorBook = new AuthorBook();
        authorBook.setAuthor(author);
        authorBook.setBook(book);

        AuthorBook saved = authorBookRepository.save(authorBook);

        return authorBookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AuthorBookResponse> getAll() {

        List<AuthorBook> authorBooks =
                authorBookRepository.findAllDetailed();

        return authorBookMapper.toResponseList(authorBooks);
    }

    @Transactional
    public void removeBookFromAuthor(Long authorId, Long bookId) {

        AuthorBook authorBook = authorBookRepository
                .findByAuthorIdAndBookId(authorId, bookId)
                .orElseThrow(() ->
                        new RuntimeException("Elaqe tapilmadi"));

        Author author = authorBook.getAuthor();
        Book book = authorBook.getBook();

        author.getAuthorBooks().remove(authorBook);
        book.getAuthorBooks().remove(authorBook);

        authorBookRepository.delete(authorBook);
    }
}
