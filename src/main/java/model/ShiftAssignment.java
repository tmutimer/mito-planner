package model;

import comparators.ShiftAssignmentDifficultyComparator;
import comparators.TaskStrengthComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Planning entity class. ShiftAssignments are linked to Tasks during planning.
 * There are as many model.ShiftAssignment instances per model.Shift instance as the Floor capacity.
 * Before planning, the task links are null, but the shift links are populated.
 */
@PlanningEntity(difficultyComparatorClass = ShiftAssignmentDifficultyComparator.class)
public class ShiftAssignment {
    //TODO to expose this constant, may need to live in another class
    private static final int TIME_UNTIL_SLOT_DIFFICULTY_WEIGHT = 1;
    @PlanningId
    private int mId;

    private static int sIdCounter = 0;

    private Shift mShift;

    // Planning variable: changes during planning, between score calculations.
    private Task mTask;

    public ShiftAssignment() {
    }

    public ShiftAssignment(Shift shift) {
        mId = ++sIdCounter;
        mShift = shift;
    }

    public int getId() {
        return mId;
    }

    public Shift getShift() {
        return mShift;
    }

    public int getShiftId() {
        return mShift.getId();
    }


    @PlanningVariable(valueRangeProviderRefs = {"taskList"}, nullable = true, strengthComparatorClass = TaskStrengthComparator.class)
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
        return !mShift.getStartTime().before(mTask.getDueDate());
    }

    public PiGroup getPiGroup() {
        if (Objects.isNull(mTask)) {
            return null;
        }
        return mTask.getPerson().getPiGroup();
    }

    public int getWeek() {
        Date date = getShift().getStartTime();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }



    @Override
    public String toString() {
        return mShift + " " + mTask;
    }

    public boolean isTaskAssigned() {
        return !Objects.isNull(mTask);
    }

    public boolean isTaskAssignedWithPrecedingTask() {
        if (!Objects.isNull(mTask)) {
            if (!Objects.isNull(mTask.getPrecedingTaskId())) {
                return true;
            }
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

    public static int getDifficulty(ShiftAssignment sa) {
        int totalDifficulty = 0;
        int daysUntil = (int) ChronoUnit.DAYS.between(LocalDate.now(), sa.getShift().getStartTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());

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
        return -1;
    }

    //TODO replace all usages of Date type with more appropriate type (LocalDate in UK timezone)
    public LocalDate getShiftTime() {
        return getShift().getStartTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public boolean isTaskAssignedWithDueDate() {
        return isTaskAssigned() && mTask.hasDueDate();
    }
}
