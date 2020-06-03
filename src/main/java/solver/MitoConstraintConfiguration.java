package solver;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@ConstraintConfiguration
public class MitoConstraintConfiguration {

    // HARD CONSTRAINTS //
    // at least 2 people must be assigned to a shift, if any are
    // (no need to specify max constraint, as handled by number of ShiftAssignments per shift instantiated in solution)
    @ConstraintWeight("Floor capacity conflict")
    private final HardSoftScore mFloorMinCapacityConflict = HardSoftScore.ofHard(10);

    // the room capacity must not be exceeded
    @ConstraintWeight("Room capacity conflict")
    private final HardSoftScore mRoomCapacityConflict = HardSoftScore.ofHard(10);

    // the person must be available for a scheduled task
    @ConstraintWeight("Person conflict")
    private final HardSoftScore mPersonConflict = HardSoftScore.ofHard(10);

    // the equipment must be available for a scheduled task
    @ConstraintWeight("Equipment conflict")
    private final HardSoftScore mEquipmentConflict = HardSoftScore.ofHard(10);

    // tasks should only be assigned once
    @ConstraintWeight("Do not repeat tasks")
    private final HardSoftScore mTaskRepeatConflict = HardSoftScore.ofHard(10);

    @ConstraintWeight("Do not double book people")
    private final HardSoftScore mPersonBookConflict = HardSoftScore.ofHard(10);


    // SOFT CONSTRAINTS //

    // as many tasks should be scheduled as possible
    @ConstraintWeight("Schedule tasks")
    private final HardSoftScore mScheduleTasks = HardSoftScore.ofSoft(10);

    // due dates should be met
    @ConstraintWeight("Due date conflict")
    private final HardSoftScore mDueDateConflict = HardSoftScore.ofSoft(25);

    // people should not have more than their limit of assigned shifts per week
    @ConstraintWeight("Shift limit conflict")
    private final HardSoftScore mShiftLimitConflict = HardSoftScore.ofSoft(30);

    // shift assignments should be fairly split between PI groups
    // TODO look at 5.4.10 for implementing this - squared workload implementation
    @ConstraintWeight("PI group unfairness")
    private final HardSoftScore mPiGroupFairness = HardSoftScore.ofSoft(1);

    // higher priority tasks should go first
    @ConstraintWeight("High priority work done")
    private final HardSoftScore mPriorityWorkDone = HardSoftScore.ofSoft(20);

}
