package model;

import java.time.LocalDateTime;
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

}
