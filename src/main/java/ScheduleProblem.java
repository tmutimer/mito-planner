import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

import java.util.List;

/**
 * Contains the solution, which is a list of completed ShiftAssignments.
 */
@PlanningSolution
public class Schedule {

    private List<Task> mTasks;
    private List<ShiftAssignment> mAssignments;
    private int mScore;

    @ValueRangeProvider(id = "taskList")
    @ProblemFactCollectionProperty
    public List<Task> getTasks() {
        return mTasks;
    }

    // TODO Double check this annotation is good.
    @PlanningEntityCollectionProperty
    public List<ShiftAssignment> getAssignments() {
        return mAssignments;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }
}
