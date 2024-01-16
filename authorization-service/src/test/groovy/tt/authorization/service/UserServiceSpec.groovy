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
import tt.authorization.entity.User
import tt.authorization.exception.UserServiceException

@SpringBootTest
@EnableSharedInjection
class UserServiceSpec extends TestcontainerSpec {

    @Autowired
    @Subject
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

    def "getAllUsers should return a list of users"() {
        when:
        def result = userService.getAllUsers();
        then:
        result.size() == expectedSize
        result.every { it instanceof User }

        where:
        expectedSize << [1]
    }

    def "getUserByEmail with email #email should return the user with email #expectedEmail"() {
        when:
        def result = userService.getUserByEmail(email);
        then:
        if (expectedEmail != null)
            result.email = expectedEmail
        else
            result == null

        where:
        email                 | expectedEmail
        testAdminEmail        | testAdminEmail
        'non_exist@gmail.com' | null
    }

    def "save user with email #email password #password and role name #role should save user to database and return valid user"() {
        given:
        def userDto = new UserDto(email, password, role)
        when:
        def result = userService.saveUser(userDto);
        then:
        result instanceof User
        result.id != null
        result.role.getName() == role
        result.email == userService.getUserByEmail(email).email
        cleanup:
        userService.deleteUser(result.id)
        where:
        email         | password         | role
        testUserEmail | testUserPassword | Role.USER.getName()
    }

    def "save user with existing email #email should throw UserServiceException with message #expectedMessage"() {
        given:
        def userDto = new UserDto(email, password, role);
        when:
        def result = userService.saveUser(userDto)
        then:
        def exception = thrown(UserServiceException)
        exception.message == expectedMessage;
        where:
        email          | password          | role                 | expectedMessage
        testAdminEmail | testAdminPassword | Role.ADMIN.getName() | "Can not save a user with email: " + testAdminEmail
    }

    def "delete existing user by id should not throw UserServiceException and delete user from database"() {
        given:
        def user = userService.saveUser(new UserDto(email, password, role));
        when:
        userService.deleteUser(user.id)
        then:
        notThrown(UserServiceException)
        userService.getUserByEmail(email) == null
        where:
        email         | password         | role
        testUserEmail | testUserPassword | Role.USER.getName()
    }

    def "get number of users by role should return the number of #expectedNumber of the role #role"() {
        when:
        def number = userService.getNumberOfUsersByRole(role)
        then:
        number == expectedNumber
        where:
        role       | expectedNumber
        Role.ADMIN | 1
        Role.USER  | 0
    }
}