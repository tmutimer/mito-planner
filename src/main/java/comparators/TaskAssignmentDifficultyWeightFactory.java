package comparators;

import javassist.NotFoundException;
import model.ScheduleSolution;
import model.Task;
import model.TaskAssignment;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * Allows determining the planning difficulty of TaskAssignments,
 * while accessing the rest of the problem facts, notably the other TaskAssignments.
 */
public class TaskAssignmentDifficultyWeightFactory implements SelectionSorterWeightFactory<ScheduleSolution, TaskAssignment> {

    // using a difficulty weight factory instead of simpler difficulty comparator so that we can access all the problem facts

    /**
     * Calculates the number of tasks that precede the input taskRecursive in a chain.
     * @param solution
     * @param taskRecursive is the *last* task in the chain
     * @return int: the number of tasks preceding taskRecursive, in a chain
     */
    private static int getNumPrecedingTasks(ScheduleSolution solution, Task taskRecursive) {
        int precedingTaskCount = 0;
        //while there's a preceding task to taskRecursive
        while (taskRecursive.hasPrecedingTask()) {
            //increase the preceding count by one
            precedingTaskCount++;
            //make the taskRecursive the preceding task
            try {
                taskRecursive = solution.getTaskForId(taskRecursive.getPrecedingTaskId());
            } catch (NotFoundException e) {
                e.printStackTrace();
                System.out.println("Terminating calculating the task chain length early, because Task" +
                        taskRecursive.getPrecedingTaskId()
                        + "could not be found.");
                break;
            }
        }
        return precedingTaskCount;
    }

    /**
     * This method calculates the difficulty for an assignment, with access to the other problem facts
     */
    private static int calculateDifficulty(ScheduleSolution solution, TaskAssignment selection) {
        // maximum difficulty when the task is at the back of a long queue of successive tasks
        Task task = selection.getTask();
        int precedingTaskCount = getNumPrecedingTasks(solution, task);

        // more difficult when contiguous with preceding task
        int immediatelyFollowsPreceding = task.immediatelyFollowsPrecedingTask()? 1 : 0;
        // difficulty increases with proximity of due date
        int daysUntilDue = (int) LocalDate.now().until(task.getDueDate(), ChronoUnit.DAYS);

        // TODO implement weightings in separate public class belonging to solution, grab from there.

        return precedingTaskCount + (5 * immediatelyFollowsPreceding) - daysUntilDue;
    }

    @Override
    public TaskAssignmentDifficultyWeight createSorterWeight(ScheduleSolution scheduleSolution, TaskAssignment selection) {
        int difficulty = calculateDifficulty(scheduleSolution, selection);
        return new TaskAssignmentDifficultyWeight(selection, difficulty);
    }


    public static class TaskAssignmentDifficultyWeight implements Comparable<TaskAssignmentDifficultyWeight> {

        private static final Comparator<TaskAssignmentDifficultyWeight> COMPARATOR = Comparator
                .comparingInt(TaskAssignmentDifficultyWeight::getDifficulty)
                .thenComparingInt(TaskAssignmentDifficultyWeight::getId);

        private final TaskAssignment mTaskAssignment;
        private final int mDifficulty;

        public TaskAssignmentDifficultyWeight(TaskAssignment assignment, int difficulty) {
            mTaskAssignment = assignment;
            mDifficulty = difficulty;
        }

        public int getDifficulty() {
            return mDifficulty;
        }

        public int getId() {
            return mTaskAssignment.getId();
        }

        @Override
        public int compareTo(TaskAssignmentDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
