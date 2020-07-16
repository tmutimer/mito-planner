package model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * The model.Shift is a half-day slot. This has a fixed relationship to model.ShiftAssignment objects,
 * which does not change during planning.
 */
public class Shift {
    private final LocalDateTime mStartTime;
    private final LocalDateTime mEndTime;
    private final int mId;
    private static int sIdCounter = 0;

    public Shift(LocalDateTime startTime, LocalDateTime endTime) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return mStartTime;
    }

    public LocalDateTime getEndTime() {
        return mEndTime;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return "Shift at " + mStartTime;
    }

    /** @return the duration of the shift as an integer number of minutes */
    public int getLength() {
        return (int) mStartTime.until(mEndTime, ChronoUnit.MINUTES);
    }
}
