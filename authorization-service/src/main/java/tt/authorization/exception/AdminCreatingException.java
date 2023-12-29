package tt.authorization.exception;

import org.springframework.boot.ExitCodeGenerator;

public class AdminCreatingException extends RuntimeException implements ExitCodeGenerator {
    public AdminCreatingException(String message) {
        super(message);
    }

    @Override
    public int getExitCode() {
        return 404;
    }
}
