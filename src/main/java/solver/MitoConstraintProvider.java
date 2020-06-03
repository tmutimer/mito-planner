package solver;

import model.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.List;
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
                scheduleHighPriorityTasks(factory)
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
                .filter(ShiftAssignment::isTaskAssignedAfterDueDate)
                .penalizeConfigurable("Due date conflict");
    }

    private Constraint scheduleHighPriorityTasks(ConstraintFactory factory) {
        ToIntFunction<ShiftAssignment> getPriority = (shiftAssignment) -> shiftAssignment.getTask().getPriority();

        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .rewardConfigurable("High priority work done", getPriority);
    }

    // this may have the side effect of grouping nulls, meaning leaving many slots unused will be penalised
    // but on the other hand does encourage some null slots to exist. Hopefully that behaviour is overridden by other constraints
    // but this constraint should not be weighted too heavily, just in case.
    private Constraint schedulePiGroupsFairly(ConstraintFactory factory) {
        ToIntBiFunction<PiGroup, Integer> getCountSquared = (piGroup, count) -> count * count;
        return factory.from(ShiftAssignment.class)
                .groupBy(ShiftAssignment::getPiGroup, count())
                .penalizeConfigurable("PI group unfairness", getCountSquared);
    }

    // TODO create constraint config
    private Constraint doNotRepeatTasks(ConstraintFactory factory) {
        return factory.from(ScheduleSolution.class)
                .penalizeConfigurable("Do not repeat tasks", ScheduleSolution::getNumTaskRepeats);

    }

}
