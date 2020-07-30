package comparators;

import model.Task;

import java.io.Serializable;
import java.util.Comparator;

/**
 * No longer used (in favour of TaskAssignmentDifficultyWeightFactory). Can probably be deleted.
 */
public class TaskDifficultyComparator implements Comparator<Task>, Serializable {

    private static final Comparator<Task> COMPARATOR = Comparator.comparingInt(Task::getDifficulty)
            .thenComparingInt(Task::getId);

    @Override
    public int compare(Task a, Task b) {
        return COMPARATOR.compare(a, b);
    }
}
