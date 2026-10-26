
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.mapper.CourseMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Transactional
    public CourseResponse create(CourseRequest request) {

        Course course = courseMapper.toEntity(request);

        Course saved = courseRepository.save(course);

        return courseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAll() {

        List<Course> courses = courseRepository.findAll();

        return courseMapper.toResponseList(courses);
    }

    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course tapilmadi: " + id));

        return courseMapper.toResponse(course);
    }
}
