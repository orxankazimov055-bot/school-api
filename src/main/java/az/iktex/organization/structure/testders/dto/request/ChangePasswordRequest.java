package az.iktex.organization.structure.testders.dto.request;

import az.iktex.organization.structure.testders.validation.StrongPassword;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

        @NotBlank(message = "{validation.required}")
        String oldPassword,

        @NotBlank(message = "{validation.required}")
        @StrongPassword
        String newPassword

) {

    @Override
    public String toString() {
        return "ChangePasswordRequest[oldPassword=***, newPassword=***]";
    }
}