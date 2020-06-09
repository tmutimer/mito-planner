package solver;

import model.*;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.Joiners.equal;

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
                // TODO this is still broken
                doNotDoubleBookPerson(factory),
                scheduleTasks(factory),
                doNotExceedLimit(factory)
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
                .filter(ShiftAssignment::isTaskAssignedByDueDate)
                .rewardConfigurable("Meet due dates");
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
        return factory.fromUniquePair(ShiftAssignment.class, equal(ShiftAssignment::getTask))
                .filter((sa, sa2) -> sa.isTaskAssigned() && sa2.isTaskAssigned())
                .penalizeConfigurable("Do not repeat tasks");
    }

    private Constraint doNotDoubleBookPerson(ConstraintFactory factory) {
        return factory.fromUniquePair(ShiftAssignment.class, equal(ShiftAssignment::getPerson), equal(ShiftAssignment::getShift))
                .filter((sa, sa2) -> sa.isTaskAssigned() && sa2.isTaskAssigned())
                .penalizeConfigurable("Do not double book people");
    }

    private Constraint scheduleTasks(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .rewardConfigurable("Schedule tasks");
    }

    //TODO figure out to implement preceeding tasks.
    // For the given shift assignment, need to hard penalize if
    // there doesn't exist a shift assignment with lower planningID (or earlier date) with the preceeding task.
//    private Constraint schedulePreceedingTasks(ConstraintFactory factory) {
//        return factory.from(ShiftAssignment.class)
//                .ifNotExists(ShiftAssignment.class, )
//    }

    private Constraint doNotExceedLimit(ConstraintFactory factory) {
        TriPredicate<Person, Integer, Integer> exceedsLimit = ((person, week, shiftCount) -> shiftCount > person.getWeeklyShiftLimit());
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .groupBy(ShiftAssignment::getPerson, ShiftAssignment::getWeek, count())
                .filter(exceedsLimit)
                .penalizeConfigurable("Shift limit conflict");
    }
}
