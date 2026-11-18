
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.BookRepository;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    // CREATE
    @Transactional
    public BookResponse create(BookRequest request) {

        // Eyni adlı kitab varsa, ERR-2501 qaytar
        if (bookRepository.existsByTitle(request.title())) {
            throw new AlreadyExistsException(
                    ErrorCode.BOOK_ALREADY_EXISTS
            );
        }

        Book book = bookMapper.toEntity(request);

        Book saved = bookRepository.save(book);

        return bookMapper.toResponse(saved);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<BookResponse> getAll() {

        List<Book> books = bookRepository.findAll();

        return bookMapper.toResponseList(books);
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.BOOK_NOT_FOUND,
                                id
                        )
                );

        return bookMapper.toResponse(book);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.BOOK_NOT_FOUND,
                                id
                        )
                );

        bookRepository.delete(book);
    }
}
