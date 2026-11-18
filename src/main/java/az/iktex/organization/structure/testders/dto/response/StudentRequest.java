
package az.iktex.organization.structure.testders.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Ad boş ola bilməz")
        @Size(min = 2, max = 50,
                message = "Ad 2-50 simvol arasında olmalıdır")
        String name,

        @NotBlank(message = "Soyad boş ola bilməz")
        @Size(min = 2, max = 50,
                message = "Soyad 2-50 simvol arasında olmalıdır")
        String surname,

        @NotNull(message = "Yaş boş ola bilməz")
        @Min(value = 1,
                message = "Yaş minimum 1 olmalıdır")
        @Max(value = 120,
                message = "Yaş maksimum 120 olmalıdır")
        Integer age

) {
}
