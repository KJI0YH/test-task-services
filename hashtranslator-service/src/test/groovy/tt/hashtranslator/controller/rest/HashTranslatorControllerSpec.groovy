package tt.hashtranslator.controller.rest

import org.spockframework.spring.EnableSharedInjection
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import tt.hashtranslator.TestEnvironment
import tt.hashtranslator.client.authorization.AuthServiceClient
import tt.hashtranslator.dto.ApplicationRequestDto
import tt.hashtranslator.exception.AuthServiceException
import tt.hashtranslator.service.ApplicationService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@EnableSharedInjection
class HashTranslatorControllerSpec extends TestEnvironment {
    @Value('${service.hash-translator.hashes.max}')
    @Shared
    private int maxHashes;

    @Autowired
    private MockMvc mockMvc

    @SpringBean
    private AuthServiceClient authServiceClient = Mock(AuthServiceClient)

    @Autowired
    private ApplicationService applicationService

    def "GET api/applications/id with existed id should return 200 status code with application"() {
        given:
        def request = new ApplicationRequestDto()
        def hashes = ["cfcd208495d565ef66e7dff9f98764da", "c4ca4238a0b923820dcc509a6f75849b", "c81e728d9d4c2f636f067f89cc14862c"]
        request.setHashes(hashes.asList())
        def application = applicationService.saveApplication(request);
        def authString = getAuthString("email", "password");
        1 * authServiceClient.login(authString)

        when:
        def result = mockMvc.perform(get("/api/applications/" + application.id)
                .header("Authorization", authString)
        )
        then:
        result
                .andExpect(status().is(200))
                .andExpect(jsonPath('$.id').value(application.id))
                .andExpect(jsonPath('$.hashes').isArray())
                .andExpect(jsonPath('$.hashes[0].hash').value('cfcd208495d565ef66e7dff9f98764da'))
                .andExpect(jsonPath('$.hashes[0].status').value('ACCEPTED'))
                .andExpect(jsonPath('$.hashes[1].hash').value('c4ca4238a0b923820dcc509a6f75849b'))
                .andExpect(jsonPath('$.hashes[1].status').value('ACCEPTED'))
                .andExpect(jsonPath('$.hashes[2].hash').value('c81e728d9d4c2f636f067f89cc14862c'))
                .andExpect(jsonPath('$.hashes[2].status').value('ACCEPTED'))
    }

    def "GET api/applications/id with non existed id #id should return 400 status code with error message"() {
        given:
        def authString = getAuthString("email", "password")
        1 * authServiceClient.login(authString)
        when:
        def result = mockMvc.perform(get("/api/applications/" + id)
                .header("Authorization", authString))
        then:
        result
                .andExpect(status().is(400))
                .andReturn().response.contentAsString == 'Application with id ' + id + ' not found'
        where:
        id << ['nonexisted']
    }

    def "GET api/applications/id with invalid auth email and auth password should return 400 status code with error message"() {
        given:
        def authString = getAuthString(email, password)
        1 * authServiceClient.login(authString) >> { throw new AuthServiceException(message) }
        when:
        def result = mockMvc.perform(get("/api/applications/id")
                .header("Authorization", authString))
        then:
        result
                .andExpect(status().is(400))
                .andReturn().response.contentAsString == message
        where:
        email   | password   | message
        'email' | 'password' | 'User with email " + email + " does not exists'
        'email' | 'password' | 'Invalid password'
    }

    def "POST api/applications with valid application should return 202 status code with application id"() {
        given:
        def authString = getAuthString("email", "password");
        1 * authServiceClient.login(authString)
        when:
        def result = mockMvc.perform(post("/api/applications")
                .header("Authorization", authString)
                .content("{\"hashes\":[\"cfcd208495d565ef66e7dff9f98764da\", \"c4ca4238a0b923820dcc509a6f75849b\", \"c81e728d9d4c2f636f067f89cc14862c\"]}")
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
        result
                .andExpect(status().is(202))
                .andExpect(jsonPath('$').isNotEmpty())
    }

    def "POST api/applications with invalid application should return 400 status code with error message"() {
        given:
        def authString = getAuthString("email", "password");
        1 * authServiceClient.login(authString)
        when:
        def result = mockMvc.perform(post("/api/applications")
                .header("Authorization", authString)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
        result
                .andExpect(status().is(400))
                .andReturn().response.contentAsString == message
        where:
        message                                               | content
        'invalid: is not a valid MD5 hash\n'                  | "{\"hashes\":[\"invalid\", \"c4ca4238a0b923820dcc509a6f75849b\", \"c81e728d9d4c2f636f067f89cc14862c\"]}"
        'Too many hashes. Max number of hashes: ' + maxHashes | generateJsonStringWithMaxHashes(maxHashes + 1)
        'Hash list is empty'                                  | "{\"hashes\":[]}"
    }

    def "POST api/applications with invalid auth email and auth password should return 400 status code with error message"() {
        given:
        def authString = getAuthString(email, password)
        1 * authServiceClient.login(authString) >> { throw new AuthServiceException(message) }
        when:
        def result = mockMvc.perform(post("/api/applications")
                .header("Authorization", authString)
                .content("{\"hashes\":[\"cfcd208495d565ef66e7dff9f98764da\", \"c4ca4238a0b923820dcc509a6f75849b\", \"c81e728d9d4c2f636f067f89cc14862c\"]}")
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
        result
                .andExpect(status().is(400))
                .andReturn().response.contentAsString == message
        where:
        email   | password   | message
        'email' | 'password' | 'User with email " + email + " does not exists'
        'email' | 'password' | 'Invalid password'
    }

    def getAuthString(String email, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }

    def generateJsonStringWithMaxHashes(int maxHashes) {
        def hashes = (1..maxHashes).collect { "1" * 32 }
        return "{\"hashes\":[${hashes.collect { "\"$it\"" }.join(", ")}]}"
    }
}