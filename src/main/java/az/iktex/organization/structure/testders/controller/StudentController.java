
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.StudentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Yeni telebe yaratmaq
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(
            @RequestBody StudentRequest request) {

        return studentService.create(request);
    }

    // Butun telebeleri getirmek
    @GetMapping
    public List<StudentResponse> getAll() {

        return studentService.getAll();
    }

    // ID ile telebe tapmaq
    @GetMapping("/{id}")
    public StudentResponse getById(
            @PathVariable Long id) {

        return studentService.getById(id);
    }

    // Telebeni kursa elave etmek
    @PostMapping("/{studentId}/courses/{courseId}")
    public StudentResponse addCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        return studentService.addCourse(studentId, courseId);
    }

    // C3 - MANDATORY
    // Transaction StudentService daxilinde acilir
    @PostMapping("/{studentId}/enroll/{courseId}")
    public StudentResponse enroll(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        return studentService.enroll(studentId, courseId);
    }

    // Telebeni silmek
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        studentService.delete(id);
    }
}
