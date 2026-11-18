package az.iktex.organization.structure.testders.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(

        @NotBlank(message = "{validation.required}")
        String refreshToken

) {

    @Override
    public String toString() {
        return "RefreshRequest[refreshToken=***]";
    }
}