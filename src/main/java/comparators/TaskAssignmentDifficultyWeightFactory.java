package comparators;

import javassist.NotFoundException;
import model.ScheduleSolution;
import model.Task;
import model.TaskAssignment;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

import java.util.Comparator;

public class TaskAssignmentDifficultyWeightFactory implements SelectionSorterWeightFactory<ScheduleSolution, TaskAssignment> {

    private static int calculateDifficulty(ScheduleSolution solution, TaskAssignment selection) {
        // maximum difficulty when the task is at the front(?) of a long queue of tasks
        if (selection.hasPrecedingTask()) {
            Task precedingTask;
            do {
                int precedingId = selection.getPrecedingTaskId();
                try {
                    precedingTask = solution.getTaskForId(precedingId);

                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            } while (precedingTask)

        }
        // difficulty increases with proximity of due date
    }

    @Override
    public TaskAssignmentDifficultyWeight createSorterWeight(ScheduleSolution scheduleSolution, TaskAssignment selection) {
        int difficulty = calculateDifficulty(scheduleSolution, selection);
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

        @Override
        public int compareTo(TaskAssignmentDifficultyWeight o) {
            return 0;
        }
    }
}
