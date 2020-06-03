package model;

import java.util.Date;

/**
 * The model.Shift is a half-day slot. This has a fixed relationship to model.ShiftAssignment objects,
 * which does not change during planning.
 */
public class Shift {
    private final Date mStartTime;
    private final Date mEndTime;

    public Shift(Date startTime, Date endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    @Override
    public String toString() {
        return "Shift at " + mStartTime;
    }
}
