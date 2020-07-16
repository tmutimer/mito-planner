package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Timeslot {
    private int mId;
    private static int sIdCounter = 0;
    private LocalDateTime mStartTime;
    private LocalDateTime mEndTime;
    private Shift mShift;
    private static final int sMinutesPerTimeSlot = 15;

    public Timeslot(LocalDateTime startTime, LocalDateTime endTime, Shift shift) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
        mShift = shift;
    }

    public static List<Timeslot> slotsFromShift(Shift shift) {
        ArrayList<Timeslot> slots = new ArrayList<>();
        LocalDateTime start = shift.getStartTime();
        int numSlots = (int) shift.getLength() / sMinutesPerTimeSlot;
        for (int i = 0; i < numSlots; i++) {
            slots.add(new Timeslot(start, start.plusMinutes(sMinutesPerTimeSlot), shift));
            start = start.plusMinutes(sMinutesPerTimeSlot);
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
