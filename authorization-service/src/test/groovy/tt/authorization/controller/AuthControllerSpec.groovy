package tt.authorization.controller

import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import tt.authorization.TestcontainerSpec

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EnableSharedInjection
class AuthControllerSpec extends TestcontainerSpec {
    @Autowired
    private MockMvc mockMvc;

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

    def "GET /api/login with email #email and password #password should return #statusCode code"() {
        expect:
        mockMvc.perform(
                get("/api/login")
                        .header("Authorization", getAuthString(email, password))
        )
                .andExpect(status().is(statusCode))

        where:
        email             | password               | statusCode
        testAdminEmail    | testAdminPassword      | 200
        testUserEmail     | testUserPassword       | 400
        'not_valid_email' | 'password'             | 400
        testAdminEmail    | 'wrong_admin_password' | 400

    }

    def getAuthString(String email, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }
}