
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SalaryService {

    private static final BigDecimal MAX_SALARY =
            new BigDecimal("10000");

    @Transactional(propagation = Propagation.REQUIRED)
    public void raise(Employee employee, BigDecimal percent) {

        if (percent == null || percent.signum() < 0) {
            throw new IllegalArgumentException(
                    "Artim faizi menfi ola bilmez"
            );
        }

        BigDecimal increase = employee.getSalary()
                .multiply(percent)
                .divide(new BigDecimal("100"));

        BigDecimal newSalary =
                employee.getSalary().add(increase);

        if (newSalary.compareTo(MAX_SALARY) > 0) {
            throw new IllegalStateException(
                    "Yeni maas 10000 AZN limitini asir!"
            );
        }

        employee.setSalary(newSalary);
    }
}
