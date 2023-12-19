package tt.authorization.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tt.authorization.data.ApiResponse;
import tt.authorization.exception.AuthServiceException;
import tt.authorization.exception.NotEnoughPermissions;
import tt.authorization.exception.UserServiceException;

@ControllerAdvice
public class GlobalExceptionHandlerController {
    @ExceptionHandler({
            AuthServiceException.class,
            UserServiceException.class
    })
    public ResponseEntity<ApiResponse> handleAuthException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
    }

    @ExceptionHandler(NotEnoughPermissions.class)
    public ResponseEntity<ApiResponse> handlePermissionsException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse(false, e.getMessage()));
    }
}
