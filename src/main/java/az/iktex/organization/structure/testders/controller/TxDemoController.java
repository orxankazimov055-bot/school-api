
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tx-demo")
@RequiredArgsConstructor
public class TxDemoController {

    private final EnrollmentService enrollmentService;

    // C3 - MANDATORY
    // Transaction olmadan cagirildigi ucun xeta verecek
    @PostMapping("/reserve-seat/{courseId}")
    public String reserveSeat(@PathVariable Long courseId) {

        enrollmentService.reserveSeat(courseId);

        return "Yer rezerv edildi";
    }
}
