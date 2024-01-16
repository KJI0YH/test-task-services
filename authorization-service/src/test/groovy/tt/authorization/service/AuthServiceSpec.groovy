package tt.authorization.service

import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Subject
import tt.authorization.TestcontainerSpec
import tt.authorization.dto.UserDto
import tt.authorization.entity.Role
import tt.authorization.exception.AuthPermissionException
import tt.authorization.exception.AuthServiceException

@SpringBootTest
@EnableSharedInjection
class AuthServiceSpec extends TestcontainerSpec {
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

    @Autowired
    @Subject
    private AuthService authService

    @Autowired
    @Shared
    private UserService userService;

    def setupSpec() {
        userService.saveUser(new UserDto(testUserEmail, testUserPassword, Role.USER.getName()));
    }
    
    def "authentication for users with valid email #email and password #password should return valid user"() {
        given:
        def authString = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        when:
        def user = authService.authentication(authString);
        then:
        user.email == expectedEmail;

        where:
        email          | password          | expectedEmail
        testAdminEmail | testAdminPassword | testAdminEmail
        testUserEmail  | testUserPassword  | testUserEmail
    }

    def "authentication for users with invalid email #email and password #password should throw AuthServiceException with message #expectedMessage"() {
        given:
        def authString = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        when:
        def user = authService.authentication(authString)
        then:
        def exception = thrown(AuthServiceException)
        exception.message == expectedMessage
        where:
        email           | password                 | expectedMessage
        testAdminEmail  | 'invalid_admin_password' | 'Invalid password'
        testUserEmail   | 'invalid_user_password'  | 'Invalid password'
        'invalid_email' | 'invalid_password'       | 'User with email invalid_email not found'
    }

    def "authorization for users with the appropriate role should not thrown AuthPermissionException"() {
        given:
        def authString = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        def user = authService.authentication(authString)
        when:
        authService.authorization(user, role)
        then:
        notThrown(AuthPermissionException)
        where:
        email          | password          | role
        testAdminEmail | testAdminPassword | Role.ADMIN
        testAdminEmail | testAdminPassword | Role.USER
        testUserEmail  | testUserPassword  | Role.USER
    }

    def "authorization for users with the role of lower priority than the requested should throw AuthPermissionException"() {
        given:
        def authString = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        def user = authService.authentication(authString)
        when:
        authService.authorization(user, role)
        then:
        thrown(AuthPermissionException)
        where:
        email         | password         | role
        testUserEmail | testUserPassword | Role.ADMIN
    }
}
