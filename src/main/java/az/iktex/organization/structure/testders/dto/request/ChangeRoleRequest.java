package az.iktex.organization.structure.testders.dto.request;

import az.iktex.organization.structure.testders.enums.Role;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(

        @NotNull(message = "{validation.required}")
        Role role

) {
}