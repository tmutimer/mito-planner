package solver;

import model.EquipmentShiftLink;
import model.RoomShiftLink;
import model.ShiftAssignment;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

public class MitoConstraints {
    private Constraint doNotExceedRoomCapacity(ConstraintFactory factory) {
        return factory.from(RoomShiftLink.class)
                .filter(RoomShiftLink::isRoomOverCapacity)
                .penalize("Room capacity conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint doNotExceedEquipmentCapacity(ConstraintFactory factory) {
        return factory.from(EquipmentShiftLink.class)
                .filter(EquipmentShiftLink::isEquipmentOverCapacity)
                .penalize("Equipment conflict", HardSoftScore.ONE_HARD);
    }


    private Constraint respectDueDates(ConstraintFactory factory) {
        return factory.from(ShiftAssignment.class)
                .filter(ShiftAssignment::isTaskAssignedAfterDueDate)
                .penalize("Due date conflict", HardSoftScore.ONE_SOFT);
    }
}
