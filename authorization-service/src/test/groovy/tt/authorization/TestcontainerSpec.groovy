package tt.authorization

import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@Testcontainers
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml")
class TestcontainerSpec extends Specification {
    private static String databaseName = "test"

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(databaseName);
    
    def setupSpec(){
        postgres.start()
        System.setProperty("spring.datasource.url", postgres.jdbcUrl);
        System.setProperty("spring.datasource.username", postgres.username);
        System.setProperty("spring.datasource.password", postgres.password);
    }
    
    def cleanupSpec(){
        postgres.stop();
    }
}