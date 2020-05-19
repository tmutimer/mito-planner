import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

/**
 * Contains the solution, which is a list of completed ShiftAssignments.
 */
@PlanningSolution
public class ScheduleProblem {

    // The NurseRoster file annotates the fields, not the getters,
    // even though the fields are private. Will do the same.
    @PlanningEntityCollectionProperty
    private List<ShiftAssignment> mAssignments;

    @ValueRangeProvider(id = "taskList")
    @ProblemFactCollectionProperty
    private List<Task> mTaskList;
    @ProblemFactCollectionProperty
    private List<Person> mPersonList;
    @ProblemFactCollectionProperty
    private List<PiGroup> mPiGroupList;
    @ProblemFactCollectionProperty
    private List<Room> mRoomList;
    @ProblemFactCollectionProperty
    private List<Shift> mShiftList;

    // TODO implement Equipment

    // TODO to improve performance, can calculate a 'Cached Problem Fact Collection' of which Tasks conflict,
    //   for example, any combination of Tasks done by the same person or Tasks which require use of
    //   the same unique piece of equipment

    // This score allows for hard and soft constraints (no medium).
    @PlanningScore
    private HardSoftScore mScore;


    // v GETTERS + SETTERS v //


    public List<ShiftAssignment> getAssignments() {
        return mAssignments;
    }

    public void setAssignments(List<ShiftAssignment> assignments) {
        mAssignments = assignments;
    }

    public List<Task> getTaskList() {
        return mTaskList;
    }

    public void setTaskList(List<Task> taskList) {
        mTaskList = taskList;
    }

    public List<Person> getPersonList() {
        return mPersonList;
    }

    public void setPersonList(List<Person> personList) {
        mPersonList = personList;
    }

    public List<PiGroup> getPiGroupList() {
        return mPiGroupList;
    }

    public void setPiGroupList(List<PiGroup> piGroupList) {
        mPiGroupList = piGroupList;
    }

    public List<Room> getRoomList() {
        return mRoomList;
    }

    public void setRoomList(List<Room> roomList) {
        mRoomList = roomList;
    }

    public List<Shift> getShiftList() {
        return mShiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        mShiftList = shiftList;
    }

    public HardSoftScore getScore() {
        return mScore;
    }

    public void setScore(HardSoftScore score) {
        mScore = score;
    }
}
