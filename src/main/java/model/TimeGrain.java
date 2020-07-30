package model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * A specific period of time, with a uniform duration across all instances.
 */

// Planning Entity class, because of the Shadow Variable implemented for
@PlanningEntity
public class TimeGrain {
    private int mId;
    private static int sIdCounter = 0;
    private LocalDateTime mStartTime;
    private LocalDateTime mEndTime;
    private Shift mShift;
    private List<TaskAssignment> mTaskAssignments;
    private static final int sMinutesPerTimeGrain = 15;

    // public constructor for planning clone creation
    public TimeGrain() {

    }

    public TimeGrain(LocalDateTime startTime, LocalDateTime endTime, Shift shift) {
        mId = ++sIdCounter;
        mStartTime = startTime;
        mEndTime = endTime;
        mShift = shift;
        mTaskAssignments = new ArrayList<>();
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

    @InverseRelationShadowVariable(sourceVariableName = "startingTimeGrain")
    public List<TaskAssignment> getTaskAssignments() {
        return mTaskAssignments;
    }

    public void setTaskAssignments(List<TaskAssignment> mTaskAssignments) {
        this.mTaskAssignments = mTaskAssignments;
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
