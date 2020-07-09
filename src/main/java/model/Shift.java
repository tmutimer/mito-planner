package model;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * The model.Shift is a half-day slot. This has a fixed relationship to model.ShiftAssignment objects,
 * which does not change during planning.
 */
public class Shift {
    private final Date mStartTime;
    private final Date mEndTime;
    private final int mId;
    private static int sIdCounter = 0;

    public Shift(Date startTime, Date endTime) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "Shift at " + mStartTime;
    }

    public int getLength() {
        return (int) mStartTime.toInstant().until(mEndTime.toInstant(), ChronoUnit.MINUTES);
    }
}
