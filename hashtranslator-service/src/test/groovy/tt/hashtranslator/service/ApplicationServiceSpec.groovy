package tt.hashtranslator.service

import org.spockframework.spring.EnableSharedInjection
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Subject
import tt.hashtranslator.TestEnvironment
import tt.hashtranslator.client.md5gromweb.MD5DecryptServiceClient
import tt.hashtranslator.dto.ApplicationRequestDto
import tt.hashtranslator.entity.Application
import tt.hashtranslator.entity.Hash
import tt.hashtranslator.entity.HashStatus
import tt.hashtranslator.exception.ApplicationServiceException
import tt.hashtranslator.exception.MapperException

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import static org.awaitility.Awaitility.await

@SpringBootTest
@EnableSharedInjection
class ApplicationServiceSpec extends TestEnvironment {
    @Value('${service.hash-translator.hashes.max}')
    @Shared
    private int maxHashes;

    @Value('${service.hash-translator.hashes.timeout}')
    @Shared
    private long timeout

    @Autowired
    @Subject
    private ApplicationService applicationService

    @SpringBean
    private MD5DecryptServiceClient md5DecryptServiceClient = Mock(MD5DecryptServiceClient)

    def "get application by existing id should return application"() {
        given:
        def request = new ApplicationRequestDto()
        def hash = "cfcd208495d565ef66e7dff9f98764da"
        request.setHashes([hash])
        def id = applicationService.saveApplication(request).id
        when:
        def application = applicationService.getApplication(id);
        then:
        application.id == id
        application.hashes[0].hash == hash
        application.hashes[0].status == HashStatus.ACCEPTED
    }

    def "get application by non existing id should throw ApplicationServiceException"() {
        when:
        def application = applicationService.getApplication(id)
        then:
        def exception = thrown(ApplicationServiceException)
        exception.message == message
        where:
        id        | message
        'invalid' | 'Application with id ' + id + ' not found'
    }

    def "save valid application request should save it to database and return valid application"() {
        given:
        def request = new ApplicationRequestDto()
        request.setHashes(hashes)
        when:
        def application = applicationService.saveApplication(request)
        then:
        application.id != null
        application.hashes.every(it -> it.status == HashStatus.ACCEPTED)

        where:
        hashes << [
                ["cfcd208495d565ef66e7dff9f98764da"],
                (1..maxHashes).collect { "1" * 32 }
        ]
    }

    def "save invalid application request should throw MapperException"() {
        given:
        def request = new ApplicationRequestDto()
        request.setHashes(hashes)
        when:
        def application = applicationService.saveApplication(request)
        then:
        def exception = thrown(MapperException)
        exception.message == message

        where:
        hashes                              | message
        []                                  | 'Hash list is empty'
        ["invalid_hash"]                    | 'invalid_hash: is not a valid MD5 hash\n'
        (0..maxHashes).collect { "1" * 32 } | 'Too many hashes. Max number of hashes: ' + maxHashes
    }

    def "get raw hashes should return list of hashes that have not been processed"() {
        given:
        def application = new Application()

        def hash0 = new Hash("cfcd208495d565ef66e7dff9f98764da")
        hash0.setStatus(HashStatus.ACCEPTED)

        def hash1 = new Hash("c4ca4238a0b923820dcc509a6f75849b")
        hash1.setStatus(HashStatus.PENDING)
        hash1.setTime(LocalDateTime.now().minus(timeout + 1, ChronoUnit.MILLIS))

        def hash2 = new Hash("c81e728d9d4c2f636f067f89cc14862c")
        hash2.setStatus(HashStatus.DECRYPTED)
        hash2.setResult("2")
        hash2.setTime(LocalDateTime.now())

        def hash3 = new Hash("1" * 32)
        hash3.setStatus(HashStatus.UNDECRYPTED)
        hash3.setResult("")
        hash3.setTime(LocalDateTime.now())

        application.setHashes([hash0, hash1, hash2, hash3])
        when:
        def hashes = applicationService.getRawHashes(application)
        then:
        hashes.size() == 2
        hashes[0].hash == "cfcd208495d565ef66e7dff9f98764da"
        hashes[0].status == HashStatus.ACCEPTED
        hashes[1].hash == "c4ca4238a0b923820dcc509a6f75849b"
        hashes[1].status == HashStatus.PENDING
    }

    def "process application should start process hashes that have not been processed"() {
        given:
        3 * md5DecryptServiceClient.decrypt(_) >> ''
        def request = new ApplicationRequestDto()
        request.setHashes(["cfcd208495d565ef66e7dff9f98764da", "c4ca4238a0b923820dcc509a6f75849b", "c81e728d9d4c2f636f067f89cc14862c"])
        def application = applicationService.saveApplication(request)
        when:
        applicationService.processApplication(application)
        then:
        await().atMost(Duration.ofSeconds(5)).untilAsserted {
            def processedApplication = applicationService.getApplication(application.id)
            processedApplication.hashes.every { it.status >= HashStatus.PENDING }
        }
    }
}