
package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.response.AuthorBookResponse;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                AuthorMapper.class,
                BookMapper.class
        }
)
public interface AuthorBookMapper {

    AuthorBookResponse toResponse(AuthorBook authorBook);

    List<AuthorBookResponse> toResponseList(
            List<AuthorBook> authorBooks
    );
}
