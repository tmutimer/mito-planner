package model;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * The model.Shift is a half-day slot. This has a fixed relationship to model.ShiftAssignment objects,
 * which does not change during planning.
 */
public class Shift {
    private Date mStartTime;
    private Date mEndTime;

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
