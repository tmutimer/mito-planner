package model;

import java.io.Serializable;
import java.util.Comparator;

public class ShiftAssignmentDifficultyComparator implements Comparator<ShiftAssignment>, Serializable {

    private static final Comparator<ShiftAssignment> COMPARATOR = Comparator.comparingInt(ShiftAssignment::getDifficulty)
            .thenComparingInt(ShiftAssignment::getId);
    @Override
    public int compare(ShiftAssignment sa1, ShiftAssignment sa2) {
        return COMPARATOR.compare(sa1, sa2);
    }
}
