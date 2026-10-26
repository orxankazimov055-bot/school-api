
package az.iktex.organization.structure.testders.response;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        BigDecimal price,
        Integer pageCount
) {
}
