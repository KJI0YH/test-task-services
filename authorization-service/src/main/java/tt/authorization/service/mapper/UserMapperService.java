package tt.authorization.service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.MapperException;
import tt.authorization.service.password.PasswordService;

import javax.validation.*;
import java.util.Set;

@Service
@Slf4j
public class UserMapperService implements MapperService<User, UserDto> {

    private final PasswordService passwordService;
    private final Validator validator;

    @Autowired
    public UserMapperService(PasswordService passwordService) {
        this.passwordService = passwordService;
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Override
    public User dtoToEntity(@Valid UserDto userDto) throws MapperException {
        log.info("Convert user DTO: " + userDto + " to entity");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        try {
            if (!violations.isEmpty())
                throw new MapperException(violations.toString());
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
}
