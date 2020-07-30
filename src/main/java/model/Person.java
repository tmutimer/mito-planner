package model;

/**
 * Problem fact class. People have an office they are associated with, but the lab bench is left to the task.
 */

/**
 * A staff member of the group.
 */
public class Person {
    private final int mId;
    private final String mName;
    private final Room mOffice;
    private final PiGroup mPiGroup;
    private final int mWeeklyShiftLimit;

    public Person(int id, String name, Room office, PiGroup piGroup, int weeklyShiftLimit) {
        mId = id;
        mName = name;
        mOffice = office;
        mPiGroup = piGroup;
        mWeeklyShiftLimit = weeklyShiftLimit;
    }

    public Room getOffice() {
        return mOffice;
    }

    public int getId() {
        return mId;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).getId() == getId();
        }
        return false;
    }

    public String getName() {
        return mName;
    }

    public PiGroup getPiGroup() {
        return mPiGroup;
    }

    public int getWeeklyShiftLimit() {
        return mWeeklyShiftLimit;
    }

    @Override
    public String toString() {
        return mName;
    }
}
