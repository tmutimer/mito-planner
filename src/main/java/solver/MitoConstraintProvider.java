package solver;

import model.*;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import java.util.function.*;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.Joiners.*;

public class MitoConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                scheduleTasks(factory),
                doNotDoubleBookPerson(factory),
                schedulePiGroupsFairly(factory),
                //TODO may need to revisit de Smet's advice about implementing alongside other soft constraints.
                // (bit.ly/3f9RcSV)
                scheduleHighPriorityTasks(factory),
                scheduleTasksWithDueDates(factory),
                respectDueDates(factory),
                doNotExceedFloorCapacity(factory),

//                ----------(PROBABLY) GOOD ABOVE THIS LINE---------
//                doNotExceedRoomCapacity(factory),
                // TODO Need a totally different approach now for equipment
//                doNotOverbookEquipment(factory),
                // TODO limits appear to be for  all time, not per week. Not a first fit issue.
//                doNotExceedLimit(factory),
                respectPrecedingTasks(factory)
                // TODO implement any other required constraints
        };
    }

    private Constraint scheduleTasks(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .rewardConfigurable("Schedule tasks");
    }

    private Constraint doNotDoubleBookPerson(ConstraintFactory factory) {
        return factory.fromUniquePair(TaskAssignment.class, equal(TaskAssignment::getPerson))
                .filter(TaskAssignment::Overlaps)
                .penalizeConfigurable("Do not double book people");
    }

    private Constraint schedulePiGroupsFairly(ConstraintFactory factory) {
        ToIntBiFunction<PiGroup, Integer> getCountSquared = (piGroup, count) -> count * count;
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .groupBy(TaskAssignment::getPiGroup, count())
                .penalizeConfigurable("PI group unfairness", getCountSquared);
    }

    private Constraint scheduleHighPriorityTasks(ConstraintFactory factory) {
        ToIntFunction<TaskAssignment> getPriority = (taskAssignment) -> taskAssignment.getTask().getPriority();

        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .rewardConfigurable("High priority work done", getPriority);
    }

    // Have this positive constraint as well as the negative constraint, because otherwise the solver
    // tends to not bother with scheduling tasks with due dates at all. Unsure if this is good practice...
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


    // TODO the hard-coded 40 is a bad idea. Should instead grab ProblemData.getTotalCapacity()
    private Constraint doNotExceedFloorCapacity(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .join(Shift.class)
                .filter(((taskAssignment, shift) -> taskAssignment.getShift() == shift))
                .groupBy(((taskAssignment, shift) -> shift))
                .join(Person.class)
                .filter((Shift::isPersonAssigned))
                .groupBy(((shift, person) -> shift), countBi())
                .filter(((shift, integer) -> integer > 40))
                .penalizeConfigurable("Floor capacity conflict");
    }

    /// BELOW HERE IS MOSTLY BROKEN GARBAGE THAT NEEDS TO BE FIXED OR REPLACED ///

    // TODO fix this by filtering correctly.
    private Constraint bookImmediatelySubsequentTasks(ConstraintFactory factory) {
        return factory.from(TaskAssignment.class)
                .filter(TaskAssignment::isTaskAssigned)
                .filter(TaskAssignment::hasPrecedingTask)
                // TODO filter for it being scheduled immediately following it's preceding task
                .penalizeConfigurable("Immediately preceding task conflict");
    }

    // TODO these probably need to be entirely re-done
//    private Constraint doNotExceedRoomCapacity(ConstraintFactory factory) {
//        return factory.from(RoomShiftLink.class)

//                .filter(RoomShiftLink::isRoomOverCapacity)
//                .penalizeConfigurable("Room capacity conflict");
//    }
//    private Constraint doNotExceedEquipmentCapacity(ConstraintFactory factory) {
//        return factory.from(EquipmentTaskLink.class)

//                .filter(EquipmentTaskLink::isEquipmentOverCapacity)

//                .penalizeConfigurable("Equipment conflict");

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
    // TODO this is broken - will currently do TimeGrain limit, not Shift limit
//    private Constraint doNotExceedLimit(ConstraintFactory factory) {
//        TriPredicate<Person, Integer, Integer> exceedsLimit = ((person, week, shiftCount) -> shiftCount > person.getWeeklyShiftLimit());
//        return factory.from(TaskAssignment.class)
//                .filter(TaskAssignment::isTaskAssigned)
//                .groupBy(TaskAssignment::getPerson, TaskAssignment::getWeek, count())
//                .filter(exceedsLimit)
//                //debugging filter
////                .filter(((person, integer, integer2) -> {
////                    System.out.println("Person: " + person.toString() + ". Week " + integer + ", Count: " + integer2);
////                    if (integer2 > person.getWeeklyShiftLimit()) {
////                        System.out.println("Count of " + integer2 + " exceeds limit of " + person.getWeeklyShiftLimit());
////                    }
////                    return integer2 > person.getWeeklyShiftLimit();
////                }))
//                .penalizeConfigurable("Shift limit conflict");

//    }
/*
This would basically be the SQL equivalent:
SELECT p.personId, s.week, count(*)
FROM
	(SELECT DISTINCT p.personId, s.shiftId
  	   FROM Person p
       JOIN TaskAssignment ta
 	     ON ta.personId = p.personId
	   JOIN Shift s
         ON ta.shiftId = s.shiftId)
GROUP BY p.personId, s.week
*/
    //TODO make this work, potentially following the structure of the above SQL
//    private Constraint doNotExceedLimit(ConstraintFactory factory) {
//        return factory.from(TaskAssignment.class)
//                .groupBy(TaskAssignment::getPerson, TaskAssignment::getShift)
//                .groupBy();
//    }

    // This has been updated, may potentially work.
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