package tt.authorization.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.MapperException;
import tt.authorization.exception.UserServiceException;
import tt.authorization.repository.UserRepository;
import tt.authorization.service.mapper.UserMapperService;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapperService userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapperService userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(@Email String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(@Valid User user) throws UserServiceException {
        try {
            log.info("Saving user: " + user);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Error when saving user: " + user + " : " + e.getMessage());
            throw new UserServiceException("Can not save a user with email: " + user.getEmail());
        }
    }

    public User saveUser(@Valid UserDto userDto) throws MapperException, UserServiceException {
        User user = userMapper.dtoToEntity(userDto);
        return saveUser(user);
    }

    public void deleteUser(@NotNull Integer id) throws UserServiceException {
        try {
            log.info("Delete user with id: " + id);
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error when deleting user with id: " + id + " : " + e.getMessage());
            throw new UserServiceException("Can not delete user with id: " + id);
        }
    }

    public Integer getNumberOfUsersByRole(Role role) {
        return userRepository.countAllByRole(role);
    }
}
