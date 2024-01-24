package tt.authorization.controller.rest

import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import tt.authorization.TestEnvironment
import tt.authorization.dto.UserDto
import tt.authorization.entity.Role
import tt.authorization.service.UserService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@EnableSharedInjection
class UserControllerSpec extends TestEnvironment {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Shared
    private UserService userService;

    @Value('${config.admin.email}')
    @Shared
    private String testAdminEmail;
    @Value('${config.admin.password}')
    @Shared
    private String testAdminPassword;
    @Value('${config.user.email}')
    @Shared
    private String testUserEmail;
    @Value('${config.user.password}')
    @Shared
    private String testUserPassword;
    @Value('${config.new.email}')
    @Shared
    private String testNewEmail;
    @Value('${config.new.password}')
    @Shared
    private String testNewPassword;

    def setupSpec() {
        userService.saveUser(new UserDto(testUserEmail, testUserPassword, Role.USER.getName()))
    }

    def cleanupSpec() {
        userService.deleteUser(userService.getUserByEmail(testUserEmail).id)
    }

    def "GET /api/users/all with auth email #email and auth password #password should return a list of users or message #message with #statusCode status code"() {
        when:
        def result = mockMvc.perform(get("/api/users/all")
                .header("Authorization", getAuthString(email, password))
        );
        then:
        result
                .andExpect(status().is(statusCode))
        and:
        if (statusCode == 200) {
            result
                    .andExpect(jsonPath('$').isArray())
                    .andExpect(jsonPath('$[0].id').isNumber())
                    .andExpect(jsonPath('$[0].email').value(testAdminEmail))
                    .andExpect(jsonPath('$[0].role').value('ADMIN'))
                    .andExpect(jsonPath('$[1].id').isNumber())
                    .andExpect(jsonPath('$[1].email').value(testUserEmail))
                    .andExpect(jsonPath('$[1].role').value('USER'))
        } else if (statusCode == 400) {
            result.andReturn().response.contentAsString == message
        }
        where:
        email           | password           | statusCode | message
        testAdminEmail  | testAdminPassword  | 200        | _
        testUserEmail   | testUserPassword   | 403        | "Minimum required role: ADMIN"
        testUserEmail   | 'invalid_password' | 400        | 'Invalid password'
        'invalid_email' | testAdminPassword  | 400        | 'User with email invalid_email not found'
    }

    def "POST /api/users with auth email #email and auth password #password should return #code status code with user body or message #message"() {
        when:
        def result = mockMvc.perform(post("/api/users")
                .header("Authorization", getAuthString(email, password))
                .content("{\"email\":\"" + newEmail + "\",\"password\":\"" + newPassword + "\",\"role\":\"" + newRole + "\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
        then:
        result
                .andExpect(status().is(code))
        and:
        if (code == 201) {
            result
                    .andExpect(jsonPath('$.id').isNumber())
                    .andExpect(jsonPath('$.email').value(newEmail))
                    .andExpect(jsonPath('$.role').value(newRole))
        } else if (code >= 400) {
            result.andReturn().response.contentAsString == message
        }
        cleanup:
        if (code == 201)
            userService.deleteUser(userService.getUserByEmail(newEmail).id)

        where:
        email           | password           | newEmail        | newPassword      | newRole        | code | message
        testAdminEmail  | testAdminPassword  | testNewEmail    | testNewPassword  | "USER"         | 201  | _
        testAdminEmail  | testAdminPassword  | testNewEmail    | testNewPassword  | "ADMIN"        | 201  | _
        testUserEmail   | testUserPassword   | testNewEmail    | testNewPassword  | "USER"         | 403  | "Minimum required role: ADMIN"
        testUserEmail   | testUserPassword   | testNewEmail    | testNewPassword  | "ADMIN"        | 403  | "Minimum required role: ADMIN"
        testAdminEmail  | 'invalid_password' | testNewEmail    | testNewPassword  | "USER"         | 400  | "Invalid password"
        'invalid_email' | testAdminPassword  | testNewEmail    | testNewPassword  | "USER"         | 400  | "User with invalid_email not found"
        testAdminEmail  | testAdminPassword  | testUserEmail   | testUserPassword | "USER"         | 400  | "Can not save a user with email: " + testUserEmail
        testAdminEmail  | testAdminPassword  | 'invalid_email' | testNewPassword  | "USER"         | 400  | _
        testAdminEmail  | testAdminPassword  | testUserEmail   | ''               | "USER"         | 400  | _
        testAdminEmail  | testAdminPassword  | testNewEmail    | testNewPassword  | "invalid_role" | 400  | _
    }

    def "DELETE /api/users/id with auth email #email and auth password #password should return #statusCode status code"() {
        given:
        def id = userService.saveUser(new UserDto(testNewEmail, testNewPassword, Role.USER.getName())).id
        when:
        def result = mockMvc.perform(delete("/api/users/" + id)
                .header("Authorization", getAuthString(email, password)))
        then:
        result.andExpect(status().is(statusCode))
        and:
        if (statusCode != 204)
            result.andReturn().response.contentAsString == message
        cleanup:
        if (statusCode != 204)
            userService.deleteUser(id);
        where:
        email           | password           | statusCode | message
        testAdminEmail  | testAdminPassword  | 204        | _
        testUserEmail   | testUserPassword   | 403        | 'Minimum required role: ADMIN'
        testUserEmail   | 'invalid_password' | 400        | 'Invalid password'
        'invalid_email' | testAdminPassword  | 400        | 'User with email invalid_email not found'
    }

    def "DELETE /api/users/id with non existing id #id should return 400 status code with message #message"() {
        when:
        def result = mockMvc.perform(delete("/api/users/" + id)
                .header("Authorization", getAuthString(testAdminEmail, testAdminPassword)))
        then:
        result.andExpect(status().isBadRequest())
        and:
        result.andReturn().response.contentAsString == message
        where:
        id | message
        42 | 'Can not delete user with id: ' + id
    }

    def getAuthString(String email, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }
}