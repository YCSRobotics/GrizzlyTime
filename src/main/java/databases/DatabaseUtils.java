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
        mainWorksheet = dbProcess.returnWorksheetData("Current");

    }

    //helper method called at beginning of each method to retrieve updated data
    private void getUpdatedData() {
        mainWorksheet = dbProcess.returnWorksheetData("Current");

    }

    //TODO should take sheet as an argument
    //grabs column data from sheet
    public ArrayList<String> getColumnData(int column, int page) {
        getUpdatedData();

        mainWorksheet = page != 0 ? mainWorksheet = loggedHours : mainWorksheet;

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

    public ArrayList<String> getRowData(int row, int page) {
        getUpdatedData();

        mainWorksheet = page != 0 ? mainWorksheet = loggedHours : mainWorksheet;

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

    //sets cell data
    public void setCellData(int row, int column, String data, int page) {

        mainWorksheet = page != 0 ? mainWorksheet = loggedHours : mainWorksheet;

        for (int x = 0; x < mainWorksheet.size(); x++) {
            if (x == row) {
                System.out.println("Successfully set data on row: " + x + " and column: " + column);
                dbProcess.updateSpreadSheet(x + 1, column + 1, data);
            }
        }
    }

    //gets specific cell data
    public String getCellData(int row, int column, int page) {
        int i = 0;

        for (String sheetRow : getColumnData(column, page)) {
            if (i == row) {
                return sheetRow;
            }

            i++;
        }

        return null;
    }

    //grab a a cell row based on its position in a column
    public int getCellRowFromColumn(String cellValue, int column, int page) {
        int i = 0;
        for (String value : getColumnData(column, page)) {
            if (value.equals(cellValue)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public int nextEmptyCellColumn(int page){

        ArrayList<String> columnData = getColumnData(Constants.STUDENTIDCOLUMN, page);
        for (int i = 0; i < columnData.size(); i++) {
            if (columnData.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;

    }

}
