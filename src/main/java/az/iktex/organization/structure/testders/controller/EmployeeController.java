
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import az.iktex.organization.structure.testders.service.EmployeeService;
import az.iktex.organization.structure.testders.service.TransferAuditService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final TransferAuditService transferAuditService;

    // Yeni isci yaratmaq
    @PostMapping("/department/{departmentId}")
    public ResponseEntity<EmployeeResponse> create(
            @PathVariable Long departmentId,
            @RequestBody EmployeeRequest request) {

        EmployeeResponse response =
                employeeService.create(departmentId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Butun iscileri getirmek
    @GetMapping
    public List<EmployeeResponse> getAll() {
        return employeeService.getAll();
    }

    // ID ile isci tapmaq
    @GetMapping("/{id}")
    public EmployeeResponse getById(
            @PathVariable Long id) {

        return employeeService.getById(id);
    }

    // Isci melumatlarini yenilemek
    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable Long id,
            @RequestBody EmployeeRequest request) {

        return employeeService.update(id, request);
    }

    // Iscini silmek
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        employeeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // C1: REQUIRED - transfer ve maas artimi
    // C2: REQUIRES_NEW - SUCCESS ve FAILED audit qeydi
    @PostMapping("/{id}/transfer")
    public EmployeeResponse transfer(
            @PathVariable Long id,
            @RequestBody TransferRequest request) {

        return transferAuditService.transfer(
                id,
                request.departmentId(),
                request.raisePercent()
        );
    }

    // Transfer ucun JSON request
    public record TransferRequest(
            Long departmentId,
            BigDecimal raisePercent
    ) {
    }
}
