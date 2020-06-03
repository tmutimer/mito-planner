package model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Objects;

/**
 * Planning entity class. ShiftAssignments are linked to Tasks during planning.
 * There are as many model.ShiftAssignment instances per model.Shift instance as the Floor capacity.
 * Before planning, the task links are null, but the shift links are populated.
 */
@PlanningEntity
public class ShiftAssignment {

    private Shift mShift;

    // Planning variable: changes during planning, between score calculations.
    private Task mTask;

    public ShiftAssignment() {
    }

    public ShiftAssignment(Shift shift) {
        mShift = shift;
    }

    @PlanningVariable(valueRangeProviderRefs = {"taskList"}, nullable = true)
    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public boolean isTaskAssignedAfterDueDate() {

        if (Objects.isNull(mTask) || Objects.isNull(mTask.getDueDate())) {
            return false;
        }
        return mShift.getStartTime().after(mTask.getDueDate());
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
