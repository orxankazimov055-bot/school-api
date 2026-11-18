package az.iktex.organization.structure.testders.security;

import az.iktex.organization.structure.testders.enums.Role;

public record AuthPrincipal(
        Long accountId,
        String username,
        Role role,
        String jti,
        String sid
) {
}