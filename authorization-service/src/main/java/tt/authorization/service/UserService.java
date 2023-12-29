package tt.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.dto.UserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.MapperException;
import tt.authorization.exception.UserServiceException;
import tt.authorization.repository.UserRepository;
import tt.authorization.service.mapper.UserMapper;

import javax.validation.Valid;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(@Valid User user) throws UserServiceException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserServiceException("Can not save a user with email: " + user.getEmail());
        }
    }

    public User saveUser(@Valid UserDto userDto) throws MapperException, UserServiceException {
        User user = userMapper.dtoToEntity(userDto);
        return saveUser(user);
    }

    public void deleteUser(Integer id) throws UserServiceException {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserServiceException("Can not delete user with id: " + id);
        }
    }

    public Integer getNumberOfUsersByRole(Role role) {
        return userRepository.countAllByRole(role);
    }
}
