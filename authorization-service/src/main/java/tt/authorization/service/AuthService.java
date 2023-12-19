package tt.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.data.User;
import tt.authorization.exception.AuthServiceException;
import tt.authorization.exception.PasswordServiceException;
import tt.authorization.service.password.PasswordService;

import java.util.Base64;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordService passwordService;

    @Autowired
    public AuthService(UserService userService, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
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
        boolean isValidPassword;
        try {
            isValidPassword = passwordService.compare(password, user.getPassword());
        } catch (PasswordServiceException e) {
            throw new AuthServiceException("Invalid password");
        }

        if (!isValidPassword)
            throw new AuthServiceException("Invalid password");

        return user;
    }
}
