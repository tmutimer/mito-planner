package solver;

import model.*;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.Joiners.*;

public class MitoConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                scheduleTasks(factory),
                doNotDoubleBookPerson(factory),
//                ----------GOOD ABOVE THIS LINE---------
                doNotExceedRoomCapacity(factory),
                respectDueDates(factory),
                // TODO may need to revisit de Smet's advice about implementing alongside other soft constraints.
                scheduleHighPriorityTasks(factory),
                schedulePiGroupsFairly(factory),

                // TODO fix this
//                doNotRepeatTasks(factory),

                // TODO we need a totally different approach now
//                doNotOverbookEquipment(factory),
                // TODO limits appear to be for  all time, not per week. Not a first fit issue.
                doNotExceedLimit(factory),
                scheduleTasksWithDueDates(factory),
                // TODO doNotOverbookRooms
                respectPrecedingTasks(factory)
        };
    }

    // Should still work
    private Constraint scheduleTasks(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .rewardConfigurable("Schedule tasks");
    }

    // Updated, may work
    private Constraint doNotDoubleBookPerson(ConstraintFactory factory) {
        return factory.fromUniquePair(TaskAssignment.class, equal(TaskAssignment::getPerson))
                .filter((ta, ta2) -> ta.Overlaps(ta2))
                .penalizeConfigurable("Do not double book people");
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
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssignedWithDueDate)
                .rewardConfigurable("Schedule tasks with due dates");
    }

    private Constraint respectDueDates(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::hasTaskMissedDueDate)
                .penalizeConfigurable("Due date conflict");
    }

    private Constraint scheduleHighPriorityTasks(ConstraintFactory factory) {
        ToIntFunction<TaskAssignment> getPriority = (shiftAssignment) -> shiftAssignment.getTask().getPriority();

        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .rewardConfigurable("High priority work done", getPriority);
    }

    private Constraint schedulePiGroupsFairly(ConstraintFactory factory) {
        ToIntBiFunction<PiGroup, Integer> getCountSquared = (piGroup, count) -> count * count;
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .groupBy(TaskAssignment::getPiGroup, count())
                .penalizeConfigurable("PI group unfairness", getCountSquared);
    }

    // TODO this is probably entirely broken
    private Constraint doNotRepeatTasks(ConstraintFactory factory) {
        return factory.fromUniquePair(TaskAssignment.class, equal(TaskAssignment::getTask))
                .filter((sa, sa2) -> sa.isTaskAssigned() && sa2.isTaskAssigned())
                .penalizeConfigurable("Do not repeat tasks");
    }


    // Am using a negative version of this constraint, so this is not needed.
//    To help get the first task in a chain scheduled
//    private Constraint schedulePrecedingTasks(ConstraintFactory factory) {
//        return factory.from(ShiftAssignment.class)
//                .ifNotExists(ShiftAssignment.class, )
//    }

    // TODO this will need to be totally reworked
    private Constraint doNotOverbookEquipment(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .join(Shift.class, equal(TaskAssignment::getShiftId, Shift::getId))
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
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .groupBy(TaskAssignment::getPerson, TaskAssignment::getWeek, count())
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

    // This has been updated, may work.
    private Constraint respectPrecedingTasks(ConstraintFactory factory) {
        BiPredicate<TaskAssignment, TaskAssignment> isPrecedingTaskScheduledInThePast =
                ((taskAssignment, taskAssignment2) -> {
                    boolean tasksMatchUp = taskAssignment.getPrecedingTaskId() == taskAssignment2.getTaskId();
                    boolean precedingTaskScheduledInThePast = taskAssignment2.isTaskAssigned() && !taskAssignment.getStartTime().isBefore(taskAssignment2.getEndTime());
                    return tasksMatchUp && precedingTaskScheduledInThePast;
                });

        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssignedWithPrecedingTask)
                .ifNotExists(TaskAssignment.class
                        , filtering(isPrecedingTaskScheduledInThePast))
                .penalizeConfigurable("Preceding task conflict");
    }
}