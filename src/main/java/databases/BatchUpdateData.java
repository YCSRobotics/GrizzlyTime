package databases;

public class BatchUpdateData<T, U, V> {

    private final int first;
    private final int second;
    private final String third;

    public BatchUpdateData(int first, int second, String third) {
        this.first = first + 1;
        this.second = second + 1;
        this.third = third;
    }

    public int getFirst() { return first; }
    public int getSecond() { return second; }
    public String getThird() { return third; }
}
