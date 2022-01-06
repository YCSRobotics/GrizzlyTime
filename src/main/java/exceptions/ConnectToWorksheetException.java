package exceptions;

/**
 * @author Dalton Smith ConnectToWorksheetException Exception thrown if connecting to the worksheet
 *     fails
 */
public class ConnectToWorksheetException extends Exception {
  public ConnectToWorksheetException(String errorMessage) {
    super(errorMessage);
  }
}
