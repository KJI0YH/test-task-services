package tt.authorization.service.password;

import tt.authorization.exception.PasswordServiceException;

public interface PasswordService {
    String encode(String password) throws PasswordServiceException;

    void compare(String password, String hash) throws PasswordServiceException;
}
