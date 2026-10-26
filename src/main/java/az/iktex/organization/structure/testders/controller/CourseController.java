
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.service.CourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(
            @RequestBody CourseRequest request) {

        return courseService.create(request);
    }

    @GetMapping
    public List<CourseResponse> getAll() {

        return courseService.getAll();
    }

    @GetMapping("/{id}")
    public CourseResponse getById(
            @PathVariable Long id) {

        return courseService.getById(id);
    }
}
