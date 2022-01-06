package exceptions;

/** @author Dalton Smith OpenCvLoadFailureException Exception thrown if opencv fails to load */
public class OpenCvLoadFailureException extends Exception {
  public OpenCvLoadFailureException(String errorMessage) {
    super(errorMessage);
  }
}
