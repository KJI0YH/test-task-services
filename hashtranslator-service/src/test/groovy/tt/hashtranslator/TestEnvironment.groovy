package tt.hashtranslator

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml")
class TestEnvironment extends Specification {

    @Container
    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:latest")
            .withReuse(false)

    static {
        mongodb.start()
        System.setProperty("spring.data.mongodb.uri", mongodb.replicaSetUrl)
    }
}