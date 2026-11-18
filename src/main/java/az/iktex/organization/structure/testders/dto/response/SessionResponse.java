package az.iktex.organization.structure.testders.dto.response;

import java.time.Instant;

public record SessionResponse(

        String sessionId,

        Instant loginAt,

        Instant expiresAt,

        boolean current

) {
}