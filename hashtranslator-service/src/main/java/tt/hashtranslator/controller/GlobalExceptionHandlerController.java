package tt.hashtranslator.controller;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tt.hashtranslator.exception.ApplicationServiceException;
import tt.hashtranslator.exception.AuthServiceException;
import tt.hashtranslator.exception.MapperException;

import java.net.SocketException;
import java.net.UnknownHostException;

@ControllerAdvice
public class GlobalExceptionHandlerController {
    @ExceptionHandler({
            AuthServiceException.class,
            ApplicationServiceException.class,
            MapperException.class
    })
    public ResponseEntity<String> handleAuthException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler({
            UnknownHostException.class,
            FeignException.class,
            SocketException.class
    })
    public ResponseEntity<String> handleConnectionExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("External service unavailable: " + e.getMessage());
    }
}
