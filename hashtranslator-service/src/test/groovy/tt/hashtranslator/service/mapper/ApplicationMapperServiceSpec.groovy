package tt.hashtranslator.service.mapper


import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Shared
import spock.lang.Specification
import tt.hashtranslator.dto.ApplicationRequestDto
import tt.hashtranslator.entity.Application
import tt.hashtranslator.entity.HashStatus
import tt.hashtranslator.exception.MapperException

@SpringBootTest
@EnableSharedInjection
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml")
class ApplicationMapperServiceSpec extends Specification {

    @Value('${service.hash-translator.hashes.max}')
    @Shared
    private int maxHashes

    @Autowired
    private ApplicationMapperService mapperService;

    def "dtoToEntity should convert valid ApplicationRequestDto to Application entity"() {
        given:
        ApplicationRequestDto applicationRequestDto = new ApplicationRequestDto()
        applicationRequestDto.setHashes(hashes)

        when:
        Application application = mapperService.dtoToEntity(applicationRequestDto)

        then:
        application.hashes.size() == hashes.size()
        application.hashes.every { it.status == HashStatus.ACCEPTED }
        where:
        hashes << [
                ["cfcd208495d565ef66e7dff9f98764da"],
                (1..maxHashes).collect { "1" * 32 }
        ]
    }

    def "dtoToEntity should throw MapperException invalid ApplicationRequestDto"() {
        given:
        ApplicationRequestDto applicationRequestDto = new ApplicationRequestDto()
        applicationRequestDto.setHashes(hashes)

        when:
        mapperService.dtoToEntity(applicationRequestDto)

        then:
        def exception = thrown(MapperException)
        exception.message == message

        where:
        hashes                              | message
        []                                  | 'Hash list is empty'
        ["invalid_hash"]                    | 'invalid_hash: is not a valid MD5 hash\n'
        (0..maxHashes).collect { "1" * 32 } | 'Too many hashes. Max number of hashes: ' + maxHashes
    }
}
