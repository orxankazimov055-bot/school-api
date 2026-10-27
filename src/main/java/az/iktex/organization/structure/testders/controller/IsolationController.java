
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.service.IsolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/isolation")
@RequiredArgsConstructor
public class IsolationController {

    private final IsolationService isolationService;

    // D1 - READ_COMMITTED
    @GetMapping("/read-committed")
    public Map<String, Object> readCommitted() {

        long count =
                isolationService.countEmployeesReadCommitted();

        return Map.of(
                "isolation", "READ_COMMITTED",
                "employeeCount", count
        );
    }

    // D2 - SERIALIZABLE
    @GetMapping("/serializable")
    public Map<String, Object> serializable() {

        long count =
                isolationService.countEmployeesSerializable();

        return Map.of(
                "isolation", "SERIALIZABLE",
                "employeeCount", count
        );
    }
}
