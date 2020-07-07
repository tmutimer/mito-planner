package model;

import org.apache.commons.lang3.mutable.MutableInt;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import solver.MitoConstraintConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains the solution, which is a list of completed ShiftAssignments.
 */
@PlanningSolution
public class ScheduleSolution {

    @ConstraintConfigurationProvider
    private final MitoConstraintConfiguration mConstraintConfiguration;

    @PlanningEntityCollectionProperty
    private Set<ShiftAssignment> mAssignments;

    @ValueRangeProvider(id = "taskList")
    @ProblemFactCollectionProperty
    private Set<Task> mTaskSet;
    @ProblemFactCollectionProperty
    private Set<Person> mPersonSet;
    @ProblemFactCollectionProperty
    private Set<PiGroup> mPiGroupSet;
    @ProblemFactCollectionProperty
    private Set<Room> mRoomSet;
    @ProblemFactCollectionProperty
    private Set<Equipment> mEquipmentSet;
    @ProblemFactCollectionProperty
    private Set<Shift> mShiftSet;
    @ProblemFactProperty
    private final int mTotalCapacity;



    public ScheduleSolution() throws Exception {
        ProblemData data = new ProblemData();
        mTaskSet = data.getTaskSet();
        mPersonSet = data.getPersonSet();
        mPiGroupSet = data.getPiGroupSet();
        mRoomSet = data.getRoomSet();
        mEquipmentSet = data.getEquipmentSet();
        mShiftSet = data.getShiftSet();
        mTotalCapacity = data.getTotalCapacity();
        mAssignments = new LinkedHashSet<>();
        //create the initialised shiftAssignments
        for (Shift shift : mShiftSet) {
            // as many shift assignments per shift as there is capacity on the floor
            for (int i = 0 ; i < mTotalCapacity; i++) {
                ShiftAssignment shiftAssignment = new ShiftAssignment(shift);
                mAssignments.add(shiftAssignment);
            }
        }
        mConstraintConfiguration = new MitoConstraintConfiguration();
    }


    // TODO to improve performance, can calculate a 'Cached Problem Fact Collection' of which Tasks definitely conflict,
    //   for example, any combination of Tasks done by the same person or Tasks which require use of
    //   the same unique piece of equipment. May need to have a second look at the issues with hard-coding
    //   hard constraints (need to be broken to escape local optima).

    // This score allows for hard and soft constraints (no medium).
    @PlanningScore
    private HardSoftScore mScore;


    // v GETTERS + SETTERS v //


    public Set<ShiftAssignment> getAssignments() {
        return mAssignments;
    }

    public void setAssignments(Set<ShiftAssignment> assignments) {
        mAssignments = assignments;
    }

    public Set<Task> getTaskSet() {
        return mTaskSet;
    }

    public void setTaskSet(Set<Task> taskSet) {
        mTaskSet = taskSet;
    }

    public Set<Person> getPersonSet() {
        return mPersonSet;
    }

    public void setPersonSet(Set<Person> personSet) {
        mPersonSet = personSet;
    }

    public Set<PiGroup> getPiGroupSet() {
        return mPiGroupSet;
    }

    public void setPiGroupSet(Set<PiGroup> piGroupSet) {
        mPiGroupSet = piGroupSet;
    }

    public Set<Room> getRoomSet() {
        return mRoomSet;
    }

    public void setRoomSet(Set<Room> roomSet) {
        mRoomSet = roomSet;
    }

    public Set<Shift> getShiftSet() {
        return mShiftSet;
    }

    public void setShiftSet(Set<Shift> shiftSet) {
        mShiftSet = shiftSet;
    }

    public HardSoftScore getScore() {
        return mScore;
    }

    public void setScore(HardSoftScore score) {
        mScore = score;
    }

    // v COMPLEX METHODS v //

    public int getNumberUnassignedTasks() {
        int totalNumTasks = mTaskSet.size();
        int totalAssignedTasks = 0;
        for (Task t : mTaskSet) {
            for (ShiftAssignment shiftAssignment : mAssignments) {
                if (shiftAssignment.getTask() == t) {
                    totalAssignedTasks += 1;
                }
            }
        }
        return totalNumTasks - totalAssignedTasks;
    }

    public Set<Task> getUnassignedTasks() {
        Set<Task> allTasks = new HashSet<>(mTaskSet);
        Set<Task> assignedTasks = new HashSet<>();
        for (ShiftAssignment sa : mAssignments) {
            if (sa.isTaskAssigned()) {
                assignedTasks.add(sa.getTask());
            }
        }
        allTasks.removeAll(assignedTasks);
        return allTasks;
    }

    public void printAllUnassignedTasks() {
        Set<Task> unassignedTasks = getUnassignedTasks();
        System.out.println("Number of unassigned tasks: " + getNumberUnassignedTasks());
        for (Task t : unassignedTasks) {
            System.out.println(t);
        }
    }

    public void printPiGroupSplit() {
        Set<ShiftAssignment> assignments = getAssignments();
        Map<PiGroup, MutableInt> piGroupDistribution = new HashMap<PiGroup, MutableInt>();
        for (PiGroup piGroup: getPiGroupSet()) {
            piGroupDistribution.put(piGroup, new MutableInt(0));
        }
        for (ShiftAssignment sa : assignments) {
            if (sa.isTaskAssigned()) {
                MutableInt count = piGroupDistribution.get(sa.getPiGroup());
                count.add(1);
            }
        }
        for (PiGroup key : piGroupDistribution.keySet()) {
            System.out.println(key.getName()  + ": " + piGroupDistribution.get(key));
        }
    }

    // v CSV EXPORT METHODS v //

    public static String[] assignmentToCsvRow(ShiftAssignment assignment) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String row = null;

        Date startDate = assignment.getShift().getStartTime();
        Date endDate = assignment.getShift().getEndTime();

        String startDateString = dateFormat.format(startDate);
        String startTimeString = timeFormat.format(startDate);

        String endDateString = dateFormat.format(endDate);
        String endTimeString = timeFormat.format(endDate);

        String personName = assignment.getTask().getPerson().getName();
        String taskName = assignment.getTask().getName();

        return new String[] {personName + " - " + taskName, startDateString, startTimeString
                , endDateString, endTimeString};
    }

    private List<String[]> getAssignmentsStringArray() {
        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] {"Subject", "Start Date", "Start Time", "End Date", "End Time"});
        for (ShiftAssignment shiftAssignment : getAssignments()) {
            if (shiftAssignment.isTaskAssigned()) {
                lines.add(assignmentToCsvRow(shiftAssignment));
            }
        }
        return lines;
    }

    private String convertToCsvString(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void writeAssignmentsToCsv() throws IOException {
        List<String[]> lines = getAssignmentsStringArray();
        File csvOutputFile = new File("mostRecentExportedSolution.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            lines.stream()
                    .map(this::convertToCsvString)
                    .forEach(pw::println);
        }
    }


}
