
package az.iktex.organization.structure.testders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    @Transactional(propagation = Propagation.MANDATORY)
    public void reserveSeat(Long courseId) {

        System.out.println(
                "Kurs üçün yer rezerv edildi. Course ID: " + courseId
        );
    }
}
