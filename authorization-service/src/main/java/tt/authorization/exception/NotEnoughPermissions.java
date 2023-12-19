package tt.authorization.exception;

public class NotEnoughPermissions extends Exception {
    public NotEnoughPermissions(String message) {
        super(message);
    }
}
