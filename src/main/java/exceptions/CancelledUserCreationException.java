package exceptions;

public class CancelledUserCreationException extends Exception{
    public CancelledUserCreationException(String errorMessage) {
        super(errorMessage);
    }
}
