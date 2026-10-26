
package az.iktex.organization.structure.testders.response;

public record AuthorBookResponse(
        Long id,
        AuthorResponse author,
        BookResponse book
) {
}
