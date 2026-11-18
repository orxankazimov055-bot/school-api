
package az.iktex.organization.structure.testders.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        String traceId,
        LocalDateTime timestamp
) {
}
