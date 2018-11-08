package databases;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {
    DatabaseProcess dbProcess = new DatabaseProcess();
    List<List<Object>> mainWorksheet = null;
    List<List<Object>> loggedHours = null;

    public DatabaseUtils() {
        //initial data
        mainWorksheet = dbProcess.returnWorksheetData("1qPUj2Pu8dHXof5Jy-VSRz13Uz354t9G7Oy4v8il97W8", "Current");
        loggedHours = dbProcess.returnWorksheetData("1qPUj2Pu8dHXof5Jy-VSRz13Uz354t9G7Oy4v8il97W8", "LoggedHours");

    }

    //helper method called at beginning of each method to retrieve updated data
    public void getUpdatedData() {
        mainWorksheet = dbProcess.returnWorksheetData("1qPUj2Pu8dHXof5Jy-VSRz13Uz354t9G7Oy4v8il97W8", "Current");
        loggedHours = dbProcess.returnWorksheetData("1qPUj2Pu8dHXof5Jy-VSRz13Uz354t9G7Oy4v8il97W8", "LoggedHours");

    }

    public void setID() {
        getUpdatedData();

    }

    public ArrayList<String> getColumnData(int column) {
        getUpdatedData();

        int i = 1;
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
        int i = 0;
        for (List rowList : mainWorksheet) {
            if (i == row) {
                System.out.println("Successfully set data on row: " + i + " and column: " + column);
                dbProcess.updateSpreadSheet("1qPUj2Pu8dHXof5Jy-VSRz13Uz354t9G7Oy4v8il97W8", i + 1, 11, "Experiment");
            }

            i++;
        }
    }

}
