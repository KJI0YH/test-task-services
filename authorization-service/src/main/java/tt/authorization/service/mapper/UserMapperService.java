package tt.authorization.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.MapperException;
import tt.authorization.service.password.PBKDF2PasswordService;

import javax.validation.Valid;

@Service
@Slf4j
public class UserMapperService implements MapperService<User, UserDto> {

    private final PBKDF2PasswordService passwordService;

    @Autowired
    public UserMapperService(PBKDF2PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public User dtoToEntity(@Valid UserDto userDto) throws MapperException {
        log.info("Convert user DTO: " + userDto + " to entity");
        try {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setPasswordHash(passwordService.encode(userDto.getPassword()));
            user.setRole(Role.valueOf(userDto.getRole()));
            return user;
        } catch (Exception e) {
            log.error("Error when converting user DTO: " + userDto + " to entity: " + e.getMessage());
            throw new MapperException("Can not convert user DTO to entity");
        }
    }

    @Override
    public UserDto entityToDto(@Valid User user) throws MapperException {
        log.info("Convert user entity: " + user + " to DTO");
        try {
            UserDto userDto = new UserDto();
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPasswordHash());
            userDto.setRole(user.getRole().getName());
            return userDto;
        } catch (Exception e) {
            log.error("Error when converting user entity: " + user + " to DTO: " + e.getMessage());
            throw new MapperException("Can not convert user entity to DTO");
        }
    }
}
