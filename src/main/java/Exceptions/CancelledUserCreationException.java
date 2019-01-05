package Exceptions;

public class CancelledUserCreationException extends Exception{
    public CancelledUserCreationException(String errorMessage) {
        super(errorMessage);
    }
}
