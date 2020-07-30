package model;

import comparators.TaskAssignmentDifficultyWeightFactory;
import comparators.TimeGrainStrengthComparator;
import org.apache.commons.lang3.NotImplementedException;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

/**
 * Planning entity class. TaskAssignments are linked to TimeGrains during planning.
 * In the solution, there are as many TaskAssignment instances as there are Tasks.
 * Before planning, the TimeGrain links (the planning variable) are null, but the Task links are populated.
 */
// TODO May need to change this to be a TaskAssignmentDifficultyComparator
@PlanningEntity(difficultyWeightFactoryClass = TaskAssignmentDifficultyWeightFactory.class)
public class TaskAssignment {
    //TODO to expose this constant, may need to live in another class
    private static final int TIME_UNTIL_SLOT_DIFFICULTY_WEIGHT = 1;

    @PlanningId
    private int mId;
    private static int sIdCounter = 0;

    // In new model, Tasks are static
    private Task mTask;

    // The TimeGrain start time is the variable!
    // used without 'm' because of issue with Inverse Relation Shadow Variable
    private TimeGrain startingTimeGrain;

    public TaskAssignment() {
    }

    public TaskAssignment(Task t) {
        mId = ++sIdCounter;
        mTask = t;
    }

    public int getId() {
        return mId;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeGrainList"}, nullable = true, strengthComparatorClass = TimeGrainStrengthComparator.class)
    public TimeGrain getStartingTimeGrain() {
        return startingTimeGrain;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain) {
        this.startingTimeGrain = startingTimeGrain;
    }

    public int getShiftId() {
        return startingTimeGrain.getShift().getId();
    }

    public Shift getShift() {
        return startingTimeGrain.getShift();
    }

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public boolean hasTaskMissedDueDate() {

        if (Objects.isNull(startingTimeGrain) || Objects.isNull(mTask.getDueDate())) {
            return false;
        }
        return startingTimeGrain.getStartTime().isAfter(mTask.getDueDate());
    }

    public PiGroup getPiGroup() {
        if (Objects.isNull(startingTimeGrain)) {
            return null;
        }
        return mTask.getPerson().getPiGroup();
    }

    public int getWeek() {
        LocalDateTime date = getStartingTimeGrain().getStartTime();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(woy);
    }


    @Override
    public String toString() {
        return startingTimeGrain + " " + mTask;
    }

    public boolean isTaskAssigned() {
        return !Objects.isNull(startingTimeGrain);
    }

    public boolean isTaskAssignedWithPrecedingTask() {
        if (!Objects.isNull(startingTimeGrain)) {
            return !Objects.isNull(mTask.getPrecedingTaskId());
        }
        return false;
    }


    public Person getPerson() {
        if (Objects.isNull(getTask())) {
            return null;
        }
        return getTask().getPerson();
    }

    // obviously this is broken
    public int getEquipmentUsage(Equipment equipment) {
        throw new NotImplementedException("Not implemented getEquipmentUsage!");
    }

    // THIS IS NOT CURRENTLY USED
    public static int getDifficulty(TaskAssignment sa) {
        int totalDifficulty = 0;
        int daysUntil = (int) ChronoUnit.DAYS.between(LocalDate.now(), sa.getStartingTimeGrain().getStartTime());

        //minimum value is zero so that weird things won't happen when due date in the past
        if (daysUntil < 0) {
            daysUntil = 0;
        }

        totalDifficulty -= (TIME_UNTIL_SLOT_DIFFICULTY_WEIGHT * daysUntil);
        return totalDifficulty;
    }

    public int getPrecedingTaskId() {
        return mTask.getPrecedingTaskId();
    }

    public int getTaskId() {
        if (isTaskAssigned()) {
            return getTask().getId();
        }
        // TODO revisit this, is returning -1 a good idea?
        return -1;
    }

    public LocalDateTime getShiftTime() {
        if (Objects.nonNull(startingTimeGrain)) {
            return getStartingTimeGrain().getStartTime();
        }
        return null;
    }

    public boolean isTaskAssignedWithDueDate() {
        return isTaskAssigned() && mTask.hasDueDate();
    }


    // TODO Find out if this is bad practice?
    public LocalDateTime getStartTime() {
        return isTaskAssigned() ? startingTimeGrain.getStartTime() : null;
    }

    public LocalDateTime getEndTime() {
        return isTaskAssigned() ? getStartTime().plusMinutes(TimeGrain.getMinutesPerTimeGrain() * mTask.getDurationInGrains()) : null;
    }

    public boolean Overlaps(TaskAssignment other) {
        if (startingTimeGrain == null || other.getStartingTimeGrain() == null) {
            return false;
        }
        // TODO ID is not particularly safe, as TimeGrains must be created in Chronological order.
        int start = startingTimeGrain.getId();
        int end = start + mTask.getDurationInGrains();
        int otherStart = other.startingTimeGrain.getId();
        int otherEnd = otherStart + other.getTask().getDurationInGrains();

        if (end <= otherStart) {
            return false;
        } else if (otherEnd <= start) {
            return false;
        }
        return true;
    }

    public boolean hasPrecedingTask() {
        return getTask().hasPrecedingTask();
    }

    public int getPersonId() {
        return getPerson().getId();
    }
}
