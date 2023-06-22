package tt.authorization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.authorization.data.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AuthorizationService {

    private final UserService userService;
    private final PasswordService passwordService;

    @Autowired
    public AuthorizationService(UserService userService, PasswordService passwordService) {
        this.userService = userService;
        this.passwordService = passwordService;
    }

    public boolean authorize(HttpServletRequest request, String... roles){

        // Get basic authorization header
        String basic = request.getHeader("authorization");

        if (basic == null || basic.equals("")){
            return false;
        }

        try{

            // Split and determine email and password
            byte[] decodedBytes = Base64.getDecoder().decode(basic.substring(basic.indexOf(" ") + 1));
            String decodedString = new String(decodedBytes);
            String[] credentials = decodedString.split(":");

            if (credentials.length != 2){
                return false;
            }

            String email = credentials[0];
            String password = credentials[1];

            // Get user by email
            User user = userService.getUserByEmail(email);
            if (user == null){
                return false;
            }

            // Check user password
            if (!passwordService.compare(password, user.getPassword())){
                return false;
            }

            // Check user role
            if (!Arrays.asList(roles).contains(user.getRoleName())){
                return false;
            }
        } catch (Exception ex){
            return false;
        }
        return true;
    }
}
