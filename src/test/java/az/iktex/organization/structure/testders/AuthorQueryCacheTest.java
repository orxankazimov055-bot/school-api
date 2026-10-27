
package az.iktex.organization.structure.testders;

import az.iktex.organization.structure.testders.repository.AuthorRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuthorQueryCacheTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void testQueryCache() {

        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        sessionFactory.getCache().evictQueryRegions();
        statistics.clear();

        // Birinci sorğu
        authorRepository.findAllByCountry("Azerbaijan");

        // İkinci dəfə eyni sorğu
        authorRepository.findAllByCountry("Azerbaijan");

        System.out.println("Query Cache Hit: "
                + statistics.getQueryCacheHitCount());

        System.out.println("Query Cache Miss: "
                + statistics.getQueryCacheMissCount());

        assertTrue(
                statistics.getQueryCacheHitCount() > 0,
                "Query Cache işləmədi!"
        );
    }
}
