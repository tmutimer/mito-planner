package comparators;

import model.TaskAssignment;

import java.io.Serializable;
import java.util.Comparator;

//TODO have another look at this, may be fubar
public class TimeslotAssignmentStrengthComparator implements Comparator<TaskAssignment>, Serializable {

    private static final Comparator<TaskAssignment> COMPARATOR = Comparator.comparingInt(TaskAssignment::getDifficulty)
            .thenComparingInt(TaskAssignment::getId);
    @Override
    public int compare(TaskAssignment sa1, TaskAssignment sa2) {
        return COMPARATOR.compare(sa1, sa2);
    }
}
