package databases;

import helpers.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    private DatabaseProcess dbProcess = new DatabaseProcess();
    private List<List<Object>> mainWorksheet;
    private List<List<Object>> loggedHours;

    public DatabaseUtils() {
        //initial data
        mainWorksheet = dbProcess.returnWorksheetData(Constants.spreadsheet, "Current");
        loggedHours = dbProcess.returnWorksheetData(Constants.spreadsheet, "LoggedHours");

    }

    //helper method called at beginning of each method to retrieve updated data
    private void getUpdatedData() {
        mainWorksheet = dbProcess.returnWorksheetData(Constants.spreadsheet, "Current");
        loggedHours = dbProcess.returnWorksheetData(Constants.spreadsheet, "LoggedHours");

    }

    public ArrayList<String> getColumnData(int column) {
        getUpdatedData();

        ArrayList<String> result = new ArrayList<>();

        for (List row: mainWorksheet) {
            try {
                result.add(row.get(column).toString());

            } catch (Exception e){
                break; //no more data

            }
        }

        return result;

    }

    public ArrayList<String> getRowData(int row) {
        getUpdatedData();

        int i = 1;
        ArrayList<String> result = new ArrayList<>();

        System.out.println(mainWorksheet.get(9).get(0).toString());

        for (List currentRow : mainWorksheet) {
            if (i == row) {
                int x = 0;
                while (true) {
                    try {
                        result.add(currentRow.get(x).toString());
                        System.out.println("Added data: " + currentRow.get(x).toString());

                    } catch (Exception e) {
                        break; //no more data;
                    }
                    x++;
                }
            }

            i++;
        }

        return result;
    }

    public void setCellData(int row, int column, String data) {
        for (int x = 0; x < mainWorksheet.size(); x++) {
            if (x == row) {
                System.out.println("Successfully set data on row: " + x + " and column: " + column);
                dbProcess.updateSpreadSheet(Constants.spreadsheet, x + 1, column + 1, data);
            }
        }
    }

    public String getCellData(int row, int column) {
        int i = 0;

        for (String sheetRow : getColumnData(column)) {
            if (i == row) {
                return sheetRow;
            }

            i++;
        }

        return null;
    }

    public int getCellRowFromColumn(String cellValue, int column) {
        int i = 0;
        for (String value : getColumnData(column)) {
            if (value.equals(cellValue)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public int nextEmptyCellColumn(){
        ArrayList<String> columnData = getColumnData(Constants.STUDENTIDCOLUMN);
        for (int i = 0; i < columnData.size(); i++) {
            if (columnData.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;

    }

}
