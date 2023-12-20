package tt.hashtranslator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tt.hashtranslator.data.ApiResponse;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.AuthServiceException;

@ControllerAdvice
public class GlobalExceptionHandlerController {
    @ExceptionHandler({
            AuthServiceException.class,
            ApplicationServiceException.class
    })
    public ResponseEntity<ApiResponse> handleAuthException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage()));
    }
}
