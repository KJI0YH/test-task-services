package tt.authorization.service;

import org.springframework.stereotype.Service;
import tt.authorization.exception.PasswordServiceException;

public interface PasswordService {
    String encode(String password) throws PasswordServiceException;

    boolean compare(String password, String hash) throws PasswordServiceException;
}
