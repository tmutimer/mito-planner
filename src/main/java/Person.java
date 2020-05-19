/**
 * Problem fact class. People have an office they are associated with, but the lab bench is left to the task.
 */

// TODO consider implementing Serializable on all problem fact classes
public class Person {
    private int mId;
    private String mName;
    private Room mOffice;
    private int mWeeklyShiftLimit;

    public Room getOffice() {
        return mOffice;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getWeeklyShiftLimit() {
        return mWeeklyShiftLimit;
    }
}
