package tt.authorization.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tt.authorization.exception.*;

@ControllerAdvice
public class GlobalExceptionHandlerController {
    @ExceptionHandler({
            AuthServiceException.class,
            UserServiceException.class,
            MapperException.class,
            PasswordServiceException.class
    })
    public ResponseEntity<String> handleAuthException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(AuthPermissionException.class)
    public ResponseEntity<String> handlePermissionsException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}
