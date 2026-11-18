package az.iktex.organization.structure.testders.request;

import az.iktex.organization.structure.testders.validation.StrongPassword;
import az.iktex.organization.structure.testders.validation.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(min = 3, max = 50)
        @Pattern(
                regexp = "^[a-zA-Z0-9._]+$",
                message = ValidationMessage.Keys.USERNAME_PATTERN
        )
        String username,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @StrongPassword
        String password

) {

    @Override
    public String toString() {
        return "RegisterRequest[" +
                "username=" + username +
                ", email=" + email +
                ", password=***]";
    }
}