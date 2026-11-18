
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.exception.NotFoundException;
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

    // CREATE
    @Transactional
    public AuthorResponse create(AuthorRequest request) {

        // Eyni ad və soyadlı müəllif varsa, ERR-2401
        if (authorRepository.existsByNameAndSurname(
                request.name(),
                request.surname()
        )) {
            throw new AlreadyExistsException(
                    ErrorCode.AUTHOR_ALREADY_EXISTS
            );
        }

        Author author = authorMapper.toEntity(request);

        Author saved = authorRepository.save(author);

        return authorMapper.toResponse(saved);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<AuthorResponse> getAll() {

        List<Author> authors = authorRepository.findAll();

        return authorMapper.toResponseList(authors);
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public AuthorResponse getById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.AUTHOR_NOT_FOUND,
                                id
                        )
                );

        return authorMapper.toResponse(author);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.AUTHOR_NOT_FOUND,
                                id
                        )
                );

        authorRepository.delete(author);
    }
}
