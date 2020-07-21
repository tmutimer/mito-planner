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
import java.time.LocalDateTime;
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
    private List<TaskAssignment> mAssignments;

    @ValueRangeProvider(id = "timeGrainList")
    @ProblemFactCollectionProperty
    private List<TimeGrain> mTimeGrainList;

    @ProblemFactCollectionProperty
    private List<Task> mTaskList;

    @ProblemFactCollectionProperty
    private List<Person> mPersonList;
    @ProblemFactCollectionProperty
    private List<PiGroup> mPiGroupList;
    @ProblemFactCollectionProperty
    private List<Room> mRoomList;
    @ProblemFactCollectionProperty
    private List<Equipment> mEquipmentList;
    @ProblemFactCollectionProperty
    private List<Shift> mShiftList;

    @ProblemFactProperty
    private final int mTotalCapacity;



    public ScheduleSolution() throws Exception {
        ProblemData data = new ProblemData();
        // for small task list
//        mTaskList = data.getTaskList();
        // for larger randomised data
        mTaskList = data.generateTaskList(200);
        mPersonList = data.getPersonList();
        mPiGroupList = data.getPiGroupList();
        mRoomList = data.getRoomList();
        mEquipmentList = data.getEquipmentList();
        mShiftList = data.getShiftList();
        mTotalCapacity = data.getTotalCapacity();
        mTimeGrainList = data.getTimeGrainList();
        mAssignments = data.getTaskAssignmentList();
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

    public MitoConstraintConfiguration getConstraintConfiguration() {
        return mConstraintConfiguration;
    }

    public List<TaskAssignment> getAssignments() {
        return mAssignments;
    }

    public void setAssignments(List<TaskAssignment> assignments) {
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

    public List<Equipment> getEquipmentList() {
        return mEquipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        mEquipmentList = equipmentList;
    }

    public List<Shift> getShiftList() {
        return mShiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        mShiftList = shiftList;
    }

    public int getTotalCapacity() {
        return mTotalCapacity;
    }

    public HardSoftScore getScore() {
        return mScore;
    }

    public void setScore(HardSoftScore score) {
        mScore = score;
    }


    // v COMPLEX METHODS v //

    public int getNumberUnassignedTasks() {
        int totalNumTasks = mTaskList.size();
        int totalAssignedTasks = 0;
        for (Task t : mTaskList) {
            for (TaskAssignment taskAssignment : mAssignments) {
                if (taskAssignment.getTask() == t) {
                    totalAssignedTasks += 1;
                }
            }
        }
        return totalNumTasks - totalAssignedTasks;
    }

    public List<Task> getUnassignedTasks() {
        List<Task> allTasks = new ArrayList<>(mTaskList);
        List<Task> assignedTasks = new ArrayList<>();
        for (TaskAssignment sa : mAssignments) {
            if (sa.isTaskAssigned()) {
                assignedTasks.add(sa.getTask());
            }
        }
        allTasks.removeAll(assignedTasks);
        return allTasks;
    }

    public void printAllUnassignedTasks() {
        List<Task> unassignedTasks = getUnassignedTasks();
        System.out.println("Number of unassigned tasks: " + getNumberUnassignedTasks());
        for (Task t : unassignedTasks) {
            System.out.println(t);
        }
    }

    public void printPiGroupSplit() {
        List<TaskAssignment> assignments = getAssignments();
        Map<PiGroup, MutableInt> piGroupDistribution = new HashMap<>();
        for (PiGroup piGroup: getPiGroupList()) {
            piGroupDistribution.put(piGroup, new MutableInt(0));
        }
        for (TaskAssignment sa : assignments) {
            if (sa.isTaskAssigned()) {
                MutableInt count = piGroupDistribution.getOrDefault(sa.getPiGroup(), new MutableInt(0));
                count.add(1);
            }
        }
        for (PiGroup key : piGroupDistribution.keySet()) {
            System.out.println(key.getName()  + ": " + piGroupDistribution.get(key));
        }
    }

    // v CSV EXPORT METHODS v //

    public static String[] assignmentToCsvRow(TaskAssignment assignment) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String row = null;

        LocalDateTime startDate = assignment.getStartingTimeGrain().getStartTime();
        LocalDateTime endDate = assignment.getStartingTimeGrain().getEndTime();

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
        for (TaskAssignment taskAssignment : getAssignments()) {
            if (taskAssignment.isTaskAssigned()) {
                lines.add(assignmentToCsvRow(taskAssignment));
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
