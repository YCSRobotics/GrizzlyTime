package exceptions;

/**
 * @author Dalton Smith ConnectToWorksheetException Exception thrown if connecting to the worksheet
 *     fails
 */
public class JsonKeyHasNoDataException extends Exception {
  public JsonKeyHasNoDataException(String errorMessage) {
    super(errorMessage);
  }
}
