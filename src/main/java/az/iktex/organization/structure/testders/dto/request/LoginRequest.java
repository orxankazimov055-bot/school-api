package az.iktex.organization.structure.testders.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "{validation.required}")
        String login,

        @NotBlank(message = "{validation.required}")
        String password

) {

    @Override
    public String toString() {
        return "LoginRequest[login=" + login
                + ", password=***]";
    }
}