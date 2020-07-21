package comparators;

import model.Task;

import java.io.Serializable;
import java.util.Comparator;

// At the moment this is not used, as Task is not a Planning Entity, but a Problem Fact
public class TaskDifficultyComparator implements Comparator<Task>, Serializable {

    private static final Comparator<Task> COMPARATOR = Comparator.comparingInt(Task::getDifficulty)
            .thenComparingInt(Task::getId);

    @Override
    public int compare(Task a, Task b) {
        return COMPARATOR.compare(a, b);
    }
}
