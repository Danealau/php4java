package php4java;

public class Php4JavaException extends Exception {
    public Php4JavaException(String message) {
        super(message);
    }

    public Php4JavaException(Exception exception) {
        super(exception);
    }
}