package tt.authorization.service.mapper


import spock.lang.Specification
import tt.authorization.dto.UserDto
import tt.authorization.entity.Role
import tt.authorization.exception.MapperException
import tt.authorization.service.password.PBKDF2PasswordService
import tt.authorization.service.password.PasswordService

class UserMapperServiceSpec extends Specification {
    def "dtoToEntity should convert valid UserDto(#email, #password, #role) to User entity"() {
        given:
        PasswordService passwordService = Mock(PBKDF2PasswordService)
        UserMapperService userMapperService = new UserMapperService(passwordService);
        def userDto = new UserDto(email, password, role.getName());
        when:
        def user = userMapperService.dtoToEntity(userDto);
        then:
        user.email == userDto.email
        user.role == role
        1 * passwordService.encode(password) >> "password_hash"
        user.passwordHash == "password_hash"
        where:
        email           | password   | role
        "test@test.com" | "password" | Role.ADMIN
        "test@test.com" | "password" | Role.USER
    }

    def "dtoToEntity should throw MapperException when trying convert invalid UserDto to User entity"() {
        given:
        PasswordService passwordService = Mock(PBKDF2PasswordService)
        UserMapperService userMapperService = new UserMapperService(passwordService);
        def userDto = new UserDto(email, password, roleName);
        when:
        def user = userMapperService.dtoToEntity(userDto);
        then:
        thrown(MapperException)
        where:
        email           | password   | roleName
        "invalid_email" | "password" | Role.ADMIN.getName()
        "test@test.com" | ""         | Role.USER.getName()
        "test@test.com" | "password" | "invalid_role"
    }
}