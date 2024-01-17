package tt.authorization.service.password;

import org.springframework.stereotype.Service;
import tt.authorization.exception.PasswordServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class BCryptPasswordService implements PasswordService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public String encode(String password) throws PasswordServiceException {
        return passwordEncoder.encode(password);
    }

    @Override
    public void compare(String password, String hash) throws PasswordServiceException {
        if (!passwordEncoder.matches(password, hash))
            throw new PasswordServiceException("Invalid password");
    }
}
