
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.entity.AuthorBook;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.BusinessException;
import az.iktex.organization.structure.testders.exception.NotFoundException;

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

    // AUTHOR-A BOOK ƏLAVƏ ET
    @Transactional
    public AuthorBookResponse addBookToAuthor(
            Long authorId, Long bookId) {

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.AUTHOR_NOT_FOUND,
                                authorId
                        )
                );

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.BOOK_NOT_FOUND,
                                bookId
                        )
                );

        // Əlaqə artıq mövcuddursa, ERR-2502
        if (authorBookRepository.existsByAuthorIdAndBookId(
                authorId, bookId
        )) {
            throw new BusinessException(
                    ErrorCode.BOOK_AUTHOR_ALREADY_LINKED
            );
        }

        AuthorBook authorBook = new AuthorBook();
        authorBook.setAuthor(author);
        authorBook.setBook(book);

        AuthorBook saved = authorBookRepository.save(authorBook);

        return authorBookMapper.toResponse(saved);
    }

    // BÜTÜN ƏLAQƏLƏRİ GÖSTƏR
    @Transactional(readOnly = true)
    public List<AuthorBookResponse> getAll() {

        List<AuthorBook> authorBooks =
                authorBookRepository.findAllDetailed();

        return authorBookMapper.toResponseList(authorBooks);
    }

    // AUTHOR-DAN BOOK ƏLAQƏSİNİ SİL
    @Transactional
    public void removeBookFromAuthor(Long authorId, Long bookId) {

        AuthorBook authorBook = authorBookRepository
                .findByAuthorIdAndBookId(authorId, bookId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.BOOK_AUTHOR_NOT_LINKED
                        )
                );

        Author author = authorBook.getAuthor();
        Book book = authorBook.getBook();

        author.getAuthorBooks().remove(authorBook);
        book.getAuthorBooks().remove(authorBook);

        authorBookRepository.delete(authorBook);
    }
}
