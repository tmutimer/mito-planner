package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Timeslot {
    private LocalDateTime mStartTime;
    private LocalDateTime mEndTime;
    private Shift mShift;
    private static final int sMinutesPerTimeSlot = 15;

    public Timeslot(LocalDateTime startTime, LocalDateTime endTime, Shift shift) {
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
