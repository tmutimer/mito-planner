package solver;

import model.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;

public class MitoConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                doNotExceedRoomCapacity(factory),
                doNotExceedEquipmentCapacity(factory),
                respectDueDates(factory),
                scheduleHighPriorityTasks(factory),
                schedulePiGroupsFairly(factory),
                doNotRepeatTasks(factory),
                doNotDoubleBookPerson(factory),
                scheduleTasks(factory)
                // TODO finish adding the other constraints when you've created them
        };
    }

    // using rewardConfigurable and penalizeConfigurable because I think this should link up to MitoConstraintConfiguration

    private Constraint doNotExceedRoomCapacity(ConstraintFactory factory) {
        return factory.from(RoomShiftLink.class)
                .filter(RoomShiftLink::isRoomOverCapacity)
                .penalizeConfigurable("Room capacity conflict");
    }

    private Constraint doNotExceedEquipmentCapacity(ConstraintFactory factory) {
        return factory.from(EquipmentShiftLink.class)
                .filter(EquipmentShiftLink::isEquipmentOverCapacity)
                .penalizeConfigurable("Equipment conflict");
    }

    private Constraint respectDueDates(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .filter(ShiftAssignment::isTaskAssignedAfterDueDate)
                .penalizeConfigurable("Due date conflict");
    }

    private Constraint scheduleHighPriorityTasks(ConstraintFactory factory) {
        ToIntFunction<ShiftAssignment> getPriority = (shiftAssignment) -> shiftAssignment.getTask().getPriority();

        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .rewardConfigurable("High priority work done", getPriority);
    }

    private Constraint schedulePiGroupsFairly(ConstraintFactory factory) {
        ToIntBiFunction<PiGroup, Integer> getCountSquared = (piGroup, count) -> count * count;
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .groupBy(ShiftAssignment::getPiGroup, count())
                .penalizeConfigurable("PI group unfairness", getCountSquared);
    }

    private Constraint doNotRepeatTasks(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .groupBy(ShiftAssignment::getTask, count())
                .filter((task, integer) -> integer > 1)
                .penalizeConfigurable("Do not repeat tasks");

    }

    private Constraint doNotDoubleBookPerson(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .groupBy(ShiftAssignment::getShift, ShiftAssignment::getPerson, count())
                .filter(((shift, person, integer) -> integer > 1))
                .penalizeConfigurable("Do not double book people");
    }

    private Constraint scheduleTasks(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .rewardConfigurable("Schedule tasks");
    }
}
