package tt.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.data.Role;
import tt.authorization.data.User;
import tt.authorization.exception.UserServiceException;
import tt.authorization.repository.UserRepository;
import tt.authorization.service.password.PasswordService;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) throws UserServiceException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserServiceException("Can not save a user with email: " + user.getEmail());
        }
    }

    public User createUser(String email, String password, Role role) throws UserServiceException {
        User user = new User();
        try {
            user.setEmail(email);
            user.setPassword(passwordService.encode(password));
            user.setRole(role);
        } catch (Exception e) {
            throw new UserServiceException("Can not create a user");
        }
        return user;
    }

    public void deleteUser(Integer id) throws UserServiceException {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new UserServiceException("Can not delete user with id: " + id);
        }
    }

    public Integer getNumberOfUsersByRole(Role role) {
        return userRepository.countAllByRoleIs(role);
    }
}
