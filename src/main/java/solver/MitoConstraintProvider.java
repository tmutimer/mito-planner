package solver;

import model.*;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.Joiners.*;

public class MitoConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                doNotExceedRoomCapacity(factory),
                respectDueDates(factory),
                // TODO may need to revisit de Smet's advice about implementing alongside other soft constraints.
                scheduleHighPriorityTasks(factory),
                schedulePiGroupsFairly(factory),
                doNotRepeatTasks(factory),
                doNotDoubleBookPerson(factory),
                scheduleTasks(factory),
                doNotOverbookEquipment(factory),
                // TODO limits appear to be for  all time, not per week. Not a first fit issue.
                doNotExceedLimit(factory),
                scheduleTasksWithDueDates(factory),
                // TODO doNotOverbookRooms
                respectPrecedingTasks(factory)
        };
    }


    private Constraint doNotExceedRoomCapacity(ConstraintFactory factory) {
        return factory.from(RoomShiftLink.class)
                .filter(RoomShiftLink::isRoomOverCapacity)
                .penalizeConfigurable("Room capacity conflict");
    }

//    private Constraint doNotExceedEquipmentCapacity(ConstraintFactory factory) {
//        return factory.from(EquipmentTaskLink.class)
//                .filter(EquipmentTaskLink::isEquipmentOverCapacity)
//                .penalizeConfigurable("Equipment conflict");
//    }

    private Constraint scheduleTasksWithDueDates(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssignedWithDueDate)
                .rewardConfigurable("Schedule tasks with due dates");
    }

    private Constraint respectDueDates(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .filter(ShiftAssignment::hasTaskMissedDueDate)
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

    private Constraint doNotOverbookEquipment(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .join(Shift.class, equal(ShiftAssignment::getShiftId, Shift::getId))
                .join(Equipment.class)
                .groupBy((shiftAssignment, shift, equipment) -> shift,
                        (shiftAssignment, shift, equipment) -> equipment,
                        sum((shiftAssignment, shift, equipment) -> shiftAssignment.getEquipmentUsage(equipment)))
                .filter((shift, equipment, integer) -> {
                    int length = shift.getLength();
                    return integer > length;
                })
                .penalizeConfigurable("Equipment conflict");
    }

    private Constraint doNotExceedLimit(ConstraintFactory factory) {
        TriPredicate<Person, Integer, Integer> exceedsLimit = ((person, week, shiftCount) -> shiftCount > person.getWeeklyShiftLimit());
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssigned)
                .groupBy(ShiftAssignment::getPerson, ShiftAssignment::getWeek, count())
                .filter(exceedsLimit)
                //debugging filter
//                .filter(((person, integer, integer2) -> {
//                    System.out.println("Person: " + person.toString() + ". Week " + integer + ", Count: " + integer2);
//                    if (integer2 > person.getWeeklyShiftLimit()) {
//                        System.out.println("Count of " + integer2 + " exceeds limit of " + person.getWeeklyShiftLimit());
//                    }
//                    return integer2 > person.getWeeklyShiftLimit();
//                }))
                .penalizeConfigurable("Shift limit conflict");
    }

    private Constraint respectPrecedingTasks(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssignedWithPrecedingTask)
                .ifNotExists(ShiftAssignment.class
                        , equal(ShiftAssignment::getPrecedingTaskId, ShiftAssignment::getTaskId)
                        , greaterThan(ShiftAssignment::getShiftTime, ShiftAssignment::getShiftTime))
                .penalizeConfigurable("Preceding task conflict");
    }
}