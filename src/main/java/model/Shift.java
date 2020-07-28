package model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The model.Shift is a half-day slot. This has a fixed relationship to model.ShiftAssignment objects,
 * which does not change during planning.
 */
public class Shift {
    private final LocalDateTime mStartTime;
    private final LocalDateTime mEndTime;
    private final int mId;
    private List<TimeGrain> mTimeGrains;
    private static int sIdCounter = 0;

    public Shift(LocalDateTime startTime, LocalDateTime endTime) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public int getWeek() {
        LocalDateTime date = getStartTime();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
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

    public List<TimeGrain> getTimeGrains() {
        return mTimeGrains;
    }

    public void setTimeGrains(List<TimeGrain> timeGrains) {
        mTimeGrains = timeGrains;
    }

    public List<Person> getAssignedPeople() {
        List<Person> assignedPeople = new ArrayList<>();
        for (TimeGrain grain : mTimeGrains) {
            for (TaskAssignment ta: grain.getTaskAssignments()) {
                assignedPeople.add(ta.getPerson());
            }
        }
        return assignedPeople;
    }

    public boolean isPersonAssigned(Person person) {
        return getAssignedPeople().contains(person);
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
