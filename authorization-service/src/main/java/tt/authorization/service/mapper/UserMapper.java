package tt.authorization.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.MapperException;
import tt.authorization.service.password.PBKDF2PasswordService;

import javax.validation.Valid;

@Service
public class UserMapper implements Mapper<User, UserDto> {

    private final PBKDF2PasswordService passwordService;

    @Autowired
    public UserMapper(PBKDF2PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Override
    public User dtoToEntity(@Valid UserDto userDto) throws MapperException {
        try {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setPasswordHash(passwordService.encode(userDto.getPassword()));
            user.setRole(Role.valueOf(userDto.getRole()));
            return user;
        } catch (Exception e) {
            throw new MapperException("Can not convert user DTO to entity");
        }
    }

    @Override
    public UserDto entityToDto(@Valid User user) throws MapperException {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPasswordHash());
        userDto.setRole(user.getRole().getName());
        return userDto;
    }
}
