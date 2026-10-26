
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Book;
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

    @Transactional
    public BookResponse create(BookRequest request) {

        Book book = bookMapper.toEntity(request);

        Book saved = bookRepository.save(book);

        return bookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAll() {

        List<Book> books = bookRepository.findAll();

        return bookMapper.toResponseList(books);
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book tapilmadi: " + id));

        return bookMapper.toResponse(book);
    }

    @Transactional
    public void delete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Book tapilmadi: " + id));

        bookRepository.delete(book);
    }
}
