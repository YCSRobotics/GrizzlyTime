package databases;

public class BatchUpdateData {

  private final int first;
  private final int second;
  private final String third;

  public BatchUpdateData(int row, int column, String data) {
    this.first = row + 1;
    this.second = column + 1;
    this.third = data;
  }

  public int getRow() {
    return first;
  }

  public int getColumn() {
    return second;
  }

  public String getData() {
    return third;
  }
}
