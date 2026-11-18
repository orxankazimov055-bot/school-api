package az.iktex.organization.structure.testders.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default ValidationMessage.Keys.PASSWORD_STRONG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}