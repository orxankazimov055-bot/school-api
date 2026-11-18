
package az.iktex.organization.structure.testders.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "{student.name.required}")
        @Size(min = 2, max = 50,
                message = "{student.name.size}")
        String name,

        @NotBlank(message = "{student.surname.required}")
        @Size(min = 2, max = 50,
                message = "{student.surname.size}")
        String surname,

        @NotNull(message = "{student.age.required}")
        @Min(value = 1,
                message = "{student.age.min}")
        @Max(value = 120,
                message = "{student.age.max}")
        Integer age

) {
}
