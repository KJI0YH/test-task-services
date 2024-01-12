package tt.authorization.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.exception.AuthServiceException;
import tt.authorization.exception.PermissionException;
import tt.authorization.exception.PasswordServiceException;
import tt.authorization.service.password.PasswordService;

import java.util.Base64;

@Service
@Slf4j
public class AuthService {

    private final UserService userService;
    private final PasswordService passwordService;

    @Autowired
    public AuthService(UserService userService, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
    }

    public void authorization(User user, Role minRole) throws PermissionException {
        if (user.getRole().getPriority() < minRole.getPriority())
            throw new PermissionException("Minimum required role: " + minRole.getName());
    }

    public User authentication(String authorization) throws AuthServiceException {
        if (authorization == null || authorization.isEmpty())
            throw new AuthServiceException("Authorization string is empty");
        // Split and determine email and password
        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getDecoder().decode(authorization.substring(authorization.indexOf(" ") + 1));
        } catch (IllegalArgumentException e) {
            throw new AuthServiceException("Illegal authorization string. Not valid Base64 scheme");
        }

        String decodedString = new String(decodedBytes);
        String[] credentials = decodedString.split(":");

        if (credentials.length != 2)
            throw new AuthServiceException("Invalid authorization string. Expected: <login>:<password>");


        String email = credentials[0];
        String password = credentials[1];

        // Get user by email
        User user = userService.getUserByEmail(email);
        if (user == null)
            throw new AuthServiceException("User with email " + email + " not found");

        // Check user password
        try {
            passwordService.compare(password, user.getPasswordHash());
        } catch (PasswordServiceException e) {
            throw new AuthServiceException("Invalid password");
        }
        return user;
    }
}
