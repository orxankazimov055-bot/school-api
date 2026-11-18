package az.iktex.organization.structure.testders.response;

import az.iktex.organization.structure.testders.enums.Role;

import java.time.Instant;

public record AccountResponse(

        Long id,

        String username,

        String email,

        Role role,

        boolean enabled,

        Instant createdAt

) {
}