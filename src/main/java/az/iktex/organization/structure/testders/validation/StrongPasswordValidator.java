package az.iktex.organization.structure.testders.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator
        implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(
            String password,
            ConstraintValidatorContext context
    ) {

        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = password.chars()
                .anyMatch(Character::isUpperCase);

        boolean hasLowerCase = password.chars()
                .anyMatch(Character::isLowerCase);

        boolean hasDigit = password.chars()
                .anyMatch(Character::isDigit);

        return hasUpperCase && hasLowerCase && hasDigit;
    }
}