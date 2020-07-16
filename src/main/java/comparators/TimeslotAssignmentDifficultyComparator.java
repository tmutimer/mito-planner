package comparators;

import model.TimeslotAssignment;

import java.io.Serializable;
import java.util.Comparator;

//TODO investigate whether actually later shifts have higher difficulty,
// because of fewer tasks being appropriate because of due dates
public class TimeslotAssignmentDifficultyComparator implements Comparator<TimeslotAssignment>, Serializable {

    private static final Comparator<TimeslotAssignment> COMPARATOR = Comparator.comparingInt(TimeslotAssignment::getDifficulty)
            .thenComparingInt(TimeslotAssignment::getId);
    @Override
    public int compare(TimeslotAssignment sa1, TimeslotAssignment sa2) {
        return COMPARATOR.compare(sa1, sa2);
    }
}
