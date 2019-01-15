package databases;

import helpers.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    /**
     * @author Dalton Smith
     * DatabaseUtils
     * Helpers to make managing the google sheets API
     * Horribly inefficient, but it works :)
     */

    private DatabaseProcess dbProcess = new DatabaseProcess();
    private List<List<Object>> mainWorksheet;
    private List<List<Object>> loggedHours;

    public DatabaseUtils() {
        //initial data
        mainWorksheet = dbProcess.returnWorksheetData(Constants.mainSheet);
        loggedHours = dbProcess.returnWorksheetData(Constants.logSheet);

    }

    //helper method called at beginning of each method to retrieve updated data
    private void getUpdatedData() {
        mainWorksheet = dbProcess.returnWorksheetData(Constants.mainSheet);
        loggedHours = dbProcess.returnWorksheetData(Constants.logSheet);

    }

    //grabs column data from sheet
    public ArrayList<String> getColumnData(int column, int page) {
        getUpdatedData();

        if (!isWorksheetsValid()) { return null; }

       setPage(page);

        ArrayList<String> result = new ArrayList<>();

        //add various rows to ArrayList
        for (List row: mainWorksheet) {
            try {
                result.add(row.get(column).toString());

            } catch (Exception e){
                 result.add("");

            }
        }

        return result;

    }

    public ArrayList<String> getRowData(int row, int page) {
        getUpdatedData();

        if (!isWorksheetsValid()) { return null; }

        setPage(page);

        int i = 1;
        ArrayList<String> result = new ArrayList<>();

        //System.out.println(mainWorksheet.get(9).get(0).toString());

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

        //set appropriate sheet
        setPage(page);

        System.out.println("Successfully set data on row: " + row + " and column: " + column+1);
        dbProcess.updateSpreadSheet(row+1, column+1, data, page);

    }

    //gets specific cell data
    public String getCellData(int row, int column, int page) {
        if (!isWorksheetsValid()) { return null; }

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

    //grab a a cell row based on its position in a column
    public int getCellRowFromColumn(String cellValue, int column, int page) {

        if (!isWorksheetsValid()) { return -1; }

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

        setPage(page);

        ArrayList<String> columnData = getColumnData(Constants.STUDENTIDCOLUMN, page);
        int i;

        for (i = 0; i < columnData.size(); i++) {
            if (columnData.get(i).isEmpty()) {
                return i;
            }
        }

        return i;

    }

    private boolean isWorksheetsValid() {
        if (mainWorksheet == null || loggedHours == null) {
            return false;
        }
        return true;
    }

    private void setPage(int page) {
        switch (page) {
            case Constants.mainSheet:
                break;
            case Constants.logSheet:
                mainWorksheet = loggedHours;
                break;
            default:
                break;


        }
    }

}
