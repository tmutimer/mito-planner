package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimeGrain {
    private int mId;
    private static int sIdCounter = 0;
    private LocalDateTime mStartTime;
    private LocalDateTime mEndTime;
    private Shift mShift;
    private static final int sMinutesPerTimeGrain = 15;

    public TimeGrain(LocalDateTime startTime, LocalDateTime endTime, Shift shift) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
        mShift = shift;
    }

    public static List<TimeGrain> fromShift(Shift shift) {
        ArrayList<TimeGrain> slots = new ArrayList<>();
        LocalDateTime start = shift.getStartTime();
        int numSlots = shift.getLength() / sMinutesPerTimeGrain;
        for (int i = 0; i < numSlots; i++) {
            slots.add(new TimeGrain(start, start.plusMinutes(sMinutesPerTimeGrain), shift));
            start = start.plusMinutes(sMinutesPerTimeGrain);
        }
        return slots;
    }

    public int getId() {
        return mId;
    }

    public LocalDateTime getStartTime() {
        return mStartTime;
    }

    public LocalDateTime getEndTime() {
        return mEndTime;
    }

    public Shift getShift() {
        return mShift;
    }


    public static int getMinutesPerTimeGrain() {
        return sMinutesPerTimeGrain;
    }

    public int getStrength() {
        // stronger planning values are those which are more likely to satisfy the planning entity. In this case,
        // this means sooner slots, because they are less likely to violate due dates

        //negative value, because the smaller the number of days, the stronger
        return (int) -LocalDate.now().until(getStartTime(), ChronoUnit.DAYS);
    }

    @Override
    public String toString() {
        return getStartTime().toString();
    }

}
