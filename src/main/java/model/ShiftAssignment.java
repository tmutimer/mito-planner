package model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Objects;

/**
 * Planning entity class. ShiftAssignments are linked to Tasks during planning.
 * There are as many model.ShiftAssignment instances per model.Shift instance as the Floor capacity.
 * Before planning, the task links are null, but the shift links are populated.
 */
@PlanningEntity
public class ShiftAssignment {
    @PlanningId
    private int mId;

    private static int sIdCounter;

    private Shift mShift;

    // Planning variable: changes during planning, between score calculations.
    private Task mTask;

    public ShiftAssignment() {
    }

    public int getId() {
        return mId;
    }

    public ShiftAssignment(Shift shift) {
        mId = ++sIdCounter;
        mShift = shift;
    }

    @PlanningVariable(valueRangeProviderRefs = {"taskList"}, nullable = true)
    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public boolean isTaskAssignedByDueDate() {

        if (Objects.isNull(mTask) || Objects.isNull(mTask.getDueDate())) {
            return false;
        }
        return mShift.getStartTime().before(mTask.getDueDate());
    }

    public PiGroup getPiGroup() {
        if (Objects.isNull(mTask)) {
            return null;
        }
        return mTask.getPerson().getPiGroup();
    }


    @Override
    public String toString() {
        return String.valueOf(mShift) + " " + mTask;
    }

    public boolean isTaskAssigned() {
        return !Objects.isNull(mTask);
    }

    public Shift getShift() {
        return mShift;
    }

    public Person getPerson() {
        if (Objects.isNull(getTask())) {
            return null;
        }
        return getTask().getPerson();
    }
}
