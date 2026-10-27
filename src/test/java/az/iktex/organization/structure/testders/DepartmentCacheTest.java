
package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.entity.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentCacheTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void testSecondLevelCache() {

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        sessionFactory.getCache().evictAllRegions();
        statistics.clear();

        Long departmentId;

        // Birinci sessiya: məlumat bazasından oxuyuruq
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            Department department = em.createQuery(
                    "SELECT d FROM Department d ORDER BY d.id",
                    Department.class
            ).setMaxResults(1).getSingleResult();

            departmentId = department.getId();
        }

        // İkinci sessiya: eyni məlumatı cache-dən oxuyuruq
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            Department department = em.find(Department.class, departmentId);
            assertNotNull(department);
        }

        // Üçüncü sessiya: cache hit yoxlanılır
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            Department department = em.find(Department.class, departmentId);
            assertNotNull(department);
        }

        System.out.println("L2 Cache Hit: "
                + statistics.getSecondLevelCacheHitCount());

        System.out.println("L2 Cache Miss: "
                + statistics.getSecondLevelCacheMissCount());

        assertTrue(statistics.getSecondLevelCacheHitCount() > 0,
                "L2 Cache hit baş vermədi!");
    }
}
