
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.mapper.AuthorMapper;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional
    public AuthorResponse create(AuthorRequest request) {

        Author author = authorMapper.toEntity(request);

        Author saved = authorRepository.save(author);

        return authorMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAll() {

        List<Author> authors = authorRepository.findAll();

        return authorMapper.toResponseList(authors);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Author tapilmadi: " + id));

        return authorMapper.toResponse(author);
    }

    @Transactional
    public void delete(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Author tapilmadi: " + id));

        authorRepository.delete(author);
    }
}
