package databases;

import activities.LoginActivity;
import helpers.Constants;
import helpers.LoggingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DatabaseUtils {
  /**
   * @author Dalton Smith DatabaseUtils Helpers to make managing the google sheets API Horribly
   *     inefficient, but it works :)
   */
  private final DatabaseProcess dbProcess = new DatabaseProcess();

  private List<List<Object>> mainWorksheet;
  private List<List<Object>> loggedHours;
  private List<List<Object>> currentWorksheet;
  private List<List<Object>> registrationData;

  // initial grab of worksheet data
  public DatabaseUtils() {
    // initial data
    currentWorksheet = dbProcess.returnWorksheetData(Constants.kMainSheet);
    mainWorksheet = currentWorksheet;
    loggedHours = dbProcess.returnWorksheetData(Constants.kLogSheet);
    updateStudentRegistrationData();
  }

  // helper method called at beginning of each method to retrieve updated data
  public void getUpdatedData() {
    currentWorksheet = dbProcess.returnWorksheetData(Constants.kMainSheet);
    mainWorksheet = currentWorksheet;
    loggedHours = dbProcess.returnWorksheetData(Constants.kLogSheet);
    updateStudentRegistrationData();
  }

  public void setCellDataBatch(ArrayList<BatchUpdateData> data, int page) {
    setPage(page);

    dbProcess.updateSpreadSheetBatch(data, page);
  }

  // update registration data sheet
  private void updateStudentRegistrationData() {
    if (LoginActivity.grizzlyPrompt) {
      registrationData = dbProcess.returnWorksheetData(Constants.kRegistrationSheet);
    }
  }

  // grabs column data from sheet
  public ArrayList<String> getColumnData(int column, int page) {
    setPage(page);

    if (isWorksheetsNotValid()) {
      return null;
    }

    ArrayList<String> result = new ArrayList<>();

    try {
      // add various rows to ArrayList
      for (List row : mainWorksheet) {
        try {
          result.add(row.get(column).toString());

        } catch (Exception e) {
          result.add("");
        }
      }
    } catch (NullPointerException e) {
      LoggingUtils.log(Level.WARNING, "IS THERE DATA IN WORKSHEET?");
    }

    return result;
  }

  public ArrayList<String> getRowData(int row, int page) {
    if (isWorksheetsNotValid()) {
      return null;
    }

    setPage(page);

    int i = 1;
    ArrayList<String> result = new ArrayList<>();

    for (List currentRow : mainWorksheet) {
      if (i == row) {
        int x = 0;
        while (true) {
          try {
            result.add(currentRow.get(x).toString());

          } catch (Exception e) {
            break; // no more data;
          }
          x++;
        }
      }

      i++;
    }

    return result;
  }

  // sets cell data
  public void setCellData(int row, int column, String data, int page) {

    // set appropriate sheet
    setPage(page);

    dbProcess.updateSpreadSheet(row + 1, column + 1, data, page);
  }

  // gets specific cell data
  public String getCellData(int row, int column, int page) {
    if (isWorksheetsNotValid()) {
      return null;
    }

    setPage(page);

    int i = 0;

    for (String sheetRow : getColumnData(column, page)) {
      if (i == row) {
        return sheetRow;
      }

      i++;
    }

    return null;
  }

  // grab a a cell row based on its position in a column
  public int getCellRowFromColumn(String cellValue, int column, int page) {

    if (isWorksheetsNotValid()) {
      return -1;
    }

    int i = 0;
    for (String value : getColumnData(column, page)) {
      if (value.equals(cellValue)) {
        return i;
      }
      i++;
    }

    return -1;
  }

  // grab the next empty cell from first row
  public int nextEmptyCellColumn(int page) {

    setPage(page);

    ArrayList<String> columnData = getColumnData(Constants.kStudentIdColumn, page);
    int i;

    for (i = 0; i < columnData.size(); i++) {
      if (columnData.get(i).isEmpty()) {
        return i;
      }
    }

    return i;
  }

  private boolean isWorksheetsNotValid() {

    if (DatabaseProcess.worksheetIsEmpty) {
      return false;
    }

    return (mainWorksheet == null || loggedHours == null);
  }

  private void setPage(int page) {
    switch (page) {
      case Constants.kMainSheet:
        mainWorksheet = currentWorksheet;
        break;
      case Constants.kLogSheet:
        mainWorksheet = loggedHours;
        break;
      case Constants.kRegistrationSheet:
        mainWorksheet = registrationData;
        break;
      default:
        break;
    }
  }
}
