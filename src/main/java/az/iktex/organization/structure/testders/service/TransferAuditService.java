
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.enums.AuditStatus;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferAuditService {

    private final EmployeeService employeeService;
    private final AuditLogService auditLogService;

    public EmployeeResponse transfer(
            Long employeeId,
            Long departmentId,
            BigDecimal raisePercent) {

        try {
            EmployeeResponse response = employeeService.transfer(
                    employeeId,
                    departmentId,
                    raisePercent
            );

            auditLogService.log(
                    "SALARY_RAISE",
                    "Employee",
                    employeeId,
                    AuditStatus.SUCCESS,
                    "Transfer ve maas artimi ugurludur"
            );

            return response;

        } catch (RuntimeException exception) {

            auditLogService.log(
                    "SALARY_RAISE",
                    "Employee",
                    employeeId,
                    AuditStatus.FAILED,
                    exception.getMessage()
            );

            throw exception;
        }
    }
}
