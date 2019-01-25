package exceptions;

public class OpenCvLoadFailureException extends Exception{
    public OpenCvLoadFailureException(String errorMessage) {
        super(errorMessage);
    }
}
