package tt.authorization.service.password;

import org.springframework.stereotype.Service;
import tt.authorization.exception.PasswordServiceException;

@Service
public interface PasswordService {
    String encode(String password) throws PasswordServiceException;

    void compare(String password, String hash) throws PasswordServiceException;
}
