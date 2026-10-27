
package az.iktex.organization.structure.testders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import az.iktex.organization.structure.testders.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class IsolationService {

    private final EmployeeRepository employeeRepository;

    // D1 - READ_COMMITTED
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            readOnly = true
    )
    public long countEmployeesReadCommitted() {

        long firstCount = employeeRepository.count();

        System.out.println(
                "READ_COMMITTED - Birinci oxuma: " + firstCount
        );

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Oxuma prosesi dayandirildi", e
            );
        }

        long secondCount = employeeRepository.count();

        System.out.println(
                "READ_COMMITTED - Ikinci oxuma: " + secondCount
        );

        return secondCount;
    }

    // D2 - SERIALIZABLE
    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            readOnly = true
    )
    public long countEmployeesSerializable() {

        long firstCount = employeeRepository.count();

        System.out.println(
                "SERIALIZABLE - Birinci oxuma: " + firstCount
        );

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Oxuma prosesi dayandirildi", e
            );
        }

        long secondCount = employeeRepository.count();

        System.out.println(
                "SERIALIZABLE - Ikinci oxuma: " + secondCount
        );

        return secondCount;
    }
}
