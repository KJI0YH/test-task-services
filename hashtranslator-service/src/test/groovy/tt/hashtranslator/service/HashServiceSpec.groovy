package tt.hashtranslator.service

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Subject
import tt.hashtranslator.TestEnvironment
import tt.hashtranslator.client.md5gromweb.MD5DecryptServiceClient
import tt.hashtranslator.dto.ApplicationRequestDto
import tt.hashtranslator.entity.HashStatus

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import static org.awaitility.Awaitility.await

@SpringBootTest
class HashServiceSpec extends TestEnvironment {
    @Autowired
    @Subject
    private HashService hashService;

    @Autowired
    private ApplicationService applicationService;

    @SpringBean
    private MD5DecryptServiceClient md5DecryptServiceClient = Mock(MD5DecryptServiceClient)

    def "update hash should update hash and store diffs to database"() {
        given:
        def request = new ApplicationRequestDto()
        request.setHashes(["cfcd208495d565ef66e7dff9f98764da"])
        def application = applicationService.saveApplication(request)
        def hash = application.hashes[0]
        when:
        hash.setStatus(HashStatus.PENDING)
        hash.setTime(LocalDateTime.now())
        hashService.updateHash(application.id, hash)
        def updatedHash = applicationService.getApplication(application.id).hashes[0]
        then:
        updatedHash.hash == hash.hash
        updatedHash.status == hash.status
        updatedHash.time.truncatedTo(ChronoUnit.SECONDS) == hash.time.truncatedTo(ChronoUnit.SECONDS)
    }

    def "process hash should start processing hash through the external service"() {
        given:
        1 * md5DecryptServiceClient.decrypt(_) >> ''
        def request = new ApplicationRequestDto()
        request.setHashes(["cfcd208495d565ef66e7dff9f98764da"])
        def application = applicationService.saveApplication(request)
        def hash = application.hashes[0]
        when:
        hashService.processHash(application.id, hash)
        then:
        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            def processedApplication = applicationService.getApplication(application.id)
            processedApplication.hashes.every { it.status == HashStatus.UNDECRYPTED }
        }

    }
}