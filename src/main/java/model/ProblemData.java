package model;

import org.drools.core.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Encapsulates the input data for the problem
 */
public class ProblemData {
    // TODO figure out whether to implement this as HashSet,
    //  may depend how much comparisons are used during planning
    private final int mTotalCapacity;
    private final Set<Room> mRoomSet;
    private final Set<Equipment> mEquipmentSet;
    private final Set<Person> mPersonSet;
    private final Set<Task> mTaskSet;
    private final Set<PiGroup> mPiGroupSet;
    private final Set<Shift> mShiftSet;

    public ProblemData() throws Exception {
        // TODO make total capacity dynamic from the appropriate csv - maybe there should be a settings.txt file.
        mTotalCapacity = 50;
        mRoomSet = createRoomSet();
        mPiGroupSet = createPiGroupSet();
        mEquipmentSet = createEquipmentSet();
        mPersonSet = createPersonSet();
        mTaskSet = createTaskSet();
        mShiftSet = createShiftSet();
    }

    // TODO add validations as per model.Person
    // model.Room //
    public Set<Room> createRoomSet() {
        Set<Room> roomSet = new LinkedHashSet<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/rooms.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int capacity = Integer.parseInt(data[3]);
                    Room room = new Room(id, name, capacity);
                    roomSet.add(room);
                }
//                for (Room r : roomSet) {
//                    System.out.println(r);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return roomSet;
    }

    // TODO see if any validations are needed
    // model.Equipment //
    public Set<Equipment> createEquipmentSet() {
        Set<Equipment> equipmentSet = new LinkedHashSet<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/equipment.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int quantity = Integer.parseInt(data[4]);
                    Equipment equipment = new Equipment(id, name, quantity);
                    equipmentSet.add(equipment);
                }
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return equipmentSet;
    }

    public Set<PiGroup> createPiGroupSet() {
        Set<PiGroup> piGroupSet = new LinkedHashSet<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/pi_groups.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    PiGroup piGroup = new PiGroup(name);
                    piGroupSet.add(piGroup);
                }
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return piGroupSet;
    }

    public Set<Person> createPersonSet() throws Exception {
        Set<Person> personSet = new LinkedHashSet<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/people.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String piGroupName = data[3];
                    PiGroup piGroup = null;

                    // get PI group
                    for (PiGroup g : mPiGroupSet) {
                        if (piGroupName.equals(g.getName())) {
                            piGroup = g;
                        }
                    }

                    String officeName = data[4];
                    Room office = null;

                    // get office
                    for (Room r : mRoomSet) {
                        String roomName = r.getRoomName();
                        if (officeName.equals(r.getRoomName())) {
                            office = r;
                        }

                    }

                    int weeklyShiftLimit = Integer.parseInt(data[5]);

                    // Validate that office was found:
                    if (Objects.isNull(office) && !StringUtils.isEmpty(officeName)) {
                        System.out.println("Failed to match " + officeName);
                        throw new Exception("Office not found! Was the name typed correctly?");
                    }

                    Person person = new Person(id, name, office, piGroup, weeklyShiftLimit);
                    personSet.add(person);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return personSet;
    }

    public Set<Task> createTaskSet() throws ParseException {
        Set<Task> taskSet = new LinkedHashSet<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/tasks.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",", -1);
                    int id = Integer.parseInt(data[0]);
                    String personString = data[1];
                    Person person = null;
                    for (Person p : mPersonSet) {
                        if (p.getName().equals(personString)) {
                            person = p;
                        }
                    }
                    String name = data[2];
                    int priority = Integer.parseInt(data[6]);
                    Date dueDate = null;
                    if (!StringUtils.isEmpty(data[5])) {
                        dueDate = new SimpleDateFormat("ddMMyyyy").parse(data[5]);
                    }

                    Integer precedingTaskId = null;
                    String precedingTaskString = data[7];
                    if (!StringUtils.isEmpty(data[7])) {
                        precedingTaskId = Integer.parseInt(data[7]);
                    }
                    // TODO get equipment reading from tasks.csv
                    Task task = new Task(id, precedingTaskId, person, name, dueDate, null, null, priority);
                    taskSet.add(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskSet;

    }

    // Currently returns a list of 100 shifts, starting from the 1st of july, every day for 50 days, 7am-12pm, 1pm-6pm
    @SuppressWarnings("SpellCheckingInspection")
    public Set<Shift> createShiftSet() {
        Set<Shift> shiftSet = new LinkedHashSet<>();
        List<Shift> startList = new ArrayList<>();
        List<Shift> endList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

        Date day = null;
        try {
            // change the string here to determine where the generated shifts start
            day = sdf.parse("01092020");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        assert day != null;
        c.setTime(day);

        Calendar morningStart = (Calendar) c.clone();
        morningStart.add(Calendar.HOUR, 7);
        Calendar morningEnd = (Calendar) c.clone();
        morningEnd.add(Calendar.HOUR, 12);

        Calendar eveningStart = (Calendar) c.clone();
        eveningStart.add(Calendar.HOUR, 13);
        Calendar eveningEnd = (Calendar) c.clone();
        eveningEnd.add(Calendar.HOUR, 18);

        for (int i = 0; i < 50; i++) {

            Shift morningShift = new Shift(morningStart.getTime(), morningEnd.getTime());
            morningStart.add(Calendar.DAY_OF_YEAR, 1);
            morningEnd.add(Calendar.DAY_OF_YEAR, 1);

            Shift eveningShift = new Shift(eveningStart.getTime(), eveningEnd.getTime());
            eveningStart.add(Calendar.DAY_OF_YEAR, 1);
            eveningEnd.add(Calendar.DAY_OF_YEAR, 1);

            shiftSet.add(morningShift);
            shiftSet.add(eveningShift);

        }

        return shiftSet;
    }

    public Set<Room> getRoomSet() {
        return mRoomSet;
    }

    public Set<Equipment> getEquipmentSet() {
        return mEquipmentSet;
    }

    public Set<Person> getPersonSet() {
        return mPersonSet;
    }

    public Set<Task> getTaskSet() {
        return mTaskSet;
    }

    public Set<PiGroup> getPiGroupSet() {
        return mPiGroupSet;
    }

    public Set<Shift> getShiftSet() {
        return mShiftSet;
    }

    public int getTotalCapacity() {
        return mTotalCapacity;
    }
}
