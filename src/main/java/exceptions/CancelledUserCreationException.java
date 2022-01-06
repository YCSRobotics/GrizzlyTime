package exceptions;

/**
 * @author Dalton Smith CancelledUserCreationException Exception thrown if user creation is
 *     cancelled
 */
public class CancelledUserCreationException extends Exception {
  public CancelledUserCreationException(String errorMessage) {
    super(errorMessage);
  }
}
