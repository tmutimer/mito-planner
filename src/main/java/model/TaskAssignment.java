package model;

import comparators.TaskDifficultyComparator;
import comparators.TimeslotAssignmentStrengthComparator;
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
 * Planning entity class. ShiftAssignments are linked to Tasks during planning.
 * There are as many model.ShiftAssignment instances per model.Shift instance as the Floor capacity.
 * Before planning, the task links are null, but the shift links are populated.
 */
// TODO May need to change this to be a TaskAssignmentDifficultyComparator
@PlanningEntity(difficultyComparatorClass = TaskDifficultyComparator.class)
public class TaskAssignment {
    //TODO to expose this constant, may need to live in another class
    private static final int TIME_UNTIL_SLOT_DIFFICULTY_WEIGHT = 1;

    @PlanningId
    private int mId;
    private static int sIdCounter = 0;

    // In new model, Tasks are static
    private Task mTask;

    // The TimeGrain start time is the variable!
    private TimeGrain mStartingTimeGrain;

    public TaskAssignment() {
    }

    public TaskAssignment(Task t) {
        mId = ++sIdCounter;
        mTask = t;
    }

    public int getId() {
        return mId;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeGrainList"}, nullable = true, strengthComparatorClass = TimeslotAssignmentStrengthComparator.class)
    public TimeGrain getStartingTimeGrain() {
        return mStartingTimeGrain;
    }

    public void setStartingTimeGrain(TimeGrain startingTimeGrain) {
        mStartingTimeGrain = startingTimeGrain;
    }

    public int getShiftId() {
        return mStartingTimeGrain.getId();
    }


    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public boolean hasTaskMissedDueDate() {

        if (Objects.isNull(mTask) || Objects.isNull(mTask.getDueDate())) {
            return false;
        }
        return !mStartingTimeGrain.getStartTime().isBefore(mTask.getDueDate());
    }

    public PiGroup getPiGroup() {
        if (Objects.isNull(mTask)) {
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
        return mStartingTimeGrain + " " + mTask;
    }

    public boolean isTaskAssigned() {
        return !Objects.isNull(mTask);
    }

    public boolean isTaskAssignedWithPrecedingTask() {
        if (!Objects.isNull(mTask)) {
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


    public int getEquipmentUsage(Equipment equipment) {
//        if(isTaskAssigned()) {
//            return getTask().getRequiredEquipment().getOrDefault(equipment, 0);
//        }
        return 0;
    }

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
        return getStartingTimeGrain().getStartTime();
    }

    public boolean isTaskAssignedWithDueDate() {
        return isTaskAssigned() && mTask.hasDueDate();
    }
}
