
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;
    private final EnrollmentService enrollmentService;

    @Transactional
    public StudentResponse create(StudentRequest request) {

        Student student = studentMapper.toEntity(request);
        Student saved = studentRepository.save(student);

        return studentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAll() {

        List<Student> students = studentRepository.findAll();

        return studentMapper.toResponseList(students);
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student tapilmadi: " + id));

        return studentMapper.toDetailResponse(student);
    }

    @Transactional
    public StudentResponse addCourse(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student tapilmadi"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course tapilmadi"));

        student.addCourse(course);

        return studentMapper.toDetailResponse(student);
    }

    // C3 - MANDATORY
    @Transactional
    public StudentResponse enroll(Long studentId, Long courseId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new RuntimeException("Student tapilmadi: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course tapilmadi: " + courseId));

        // MANDATORY metodu movcud transaction daxilinde cagiririq
        enrollmentService.reserveSeat(courseId);

        student.addCourse(course);

        return studentMapper.toDetailResponse(student);
    }

    @Transactional
    public void delete(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student tapilmadi"));

        for (Course course : new HashSet<>(student.getCourses())) {
            student.removeCourse(course);
        }

        studentRepository.delete(student);
    }
}
