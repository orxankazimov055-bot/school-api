package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.StudentService;
import az.iktex.organization.structure.testders.dto.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(
        name = "Student API",
        description = "Telebelerin idare olunmasi"
)
public class StudentController {

    private final StudentService studentService;

    @Operation(
            summary = "Telebe yarat",
            description = "Yeni telebe qeydiyyata alir"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Telebe yaradildi"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation xetasi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(
            @RequestBody @jakarta.validation.Valid StudentRequest request) {

        return studentService.create(request);
    }

    @Operation(summary = "Butun telebeleri getir")
    @ApiResponse(
            responseCode = "200",
            description = "Telebelerin siyahisi"
    )
    @GetMapping
    public List<StudentResponse> getAll() {
        return studentService.getAll();
    }

    @Operation(summary = "ID ile telebe getir")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Telebe tapildi"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Telebe tapilmadi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public StudentResponse getById(
            @PathVariable Long id) {

        return studentService.getById(id);
    }

    @Operation(summary = "Telebeye kurs elave et")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Kurs elave edildi"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Telebe ve ya kurs tapilmadi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/{studentId}/courses/{courseId}")
    public StudentResponse addCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        return studentService.addCourse(studentId, courseId);
    }

    @Operation(summary = "Telebeni sil")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Telebe silindi"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Telebe tapilmadi",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}