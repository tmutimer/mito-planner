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
    private final List<Room> mRoomList;
    private final List<Equipment> mEquipmentList;
    private final List<Person> mPersonList;
    private final List<Task> mTaskList;
    private final List<PiGroup> mPiGroupList;
    private final List<Shift> mShiftList;

    public ProblemData() throws Exception {
        // TODO make total capacity dynamic from the appropriate csv - maybe there should be a settings.txt file.
        mTotalCapacity = 50;
        mRoomList = createRoomList();
        mPiGroupList = createPiGroupList();
        mEquipmentList = createEquipmentList();
        mPersonList = createPersonList();
        mTaskList = createTaskList();
        mShiftList = createShiftList();
    }

    // TODO add validations as per model.Person
    // model.Room //
    public List<Room> createRoomList() {
        List<Room> roomList = new ArrayList<>();
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
                    roomList.add(room);
                }
//                for (Room r : roomList) {
//                    System.out.println(r);
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return roomList;
    }

    // TODO see if any validations are needed
    // model.Equipment //
    public List<Equipment> createEquipmentList() {
        List<Equipment> equipmentList = new ArrayList<>();
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
                    equipmentList.add(equipment);
                }
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return equipmentList;
    }

    public List<PiGroup> createPiGroupList() {
        List<PiGroup> piGroupList = new ArrayList<>();
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
                    piGroupList.add(piGroup);
                }
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return piGroupList;
    }

    public List<Person> createPersonList() throws Exception {
        List<Person> personList = new ArrayList<>();
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
                    for (PiGroup g : mPiGroupList) {
                        if (piGroupName.equals(g.getName())) {
                            piGroup = g;
                        }
                    }

                    String officeName = data[4];
                    Room office = null;

                    // get office
                    for (Room r : mRoomList) {
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
                    personList.add(person);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return personList;
    }

    public List<Task> createTaskList() throws ParseException {
        List<Task> taskList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("/Users/Tom/IdeaProjects/mito-planner/src/main/resources/tasks.csv"));
                //skip header
                csvReader.readLine();
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String personString = data[1];
                    Person person = null;
                    for (Person p : mPersonList) {
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

                    // TODO get equipment reading from tasks.csv
                    Task task = new Task(id, person, name, dueDate, null, null, priority);
                    taskList.add(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskList;

    }

    // Currently returns a list of 100 shifts, starting from the 1st of july, every day for 50 days, 7am-12pm, 1pm-6pm
    @SuppressWarnings("SpellCheckingInspection")
    public List<Shift> createShiftList() {
        List<Shift> shiftList = new ArrayList<>();
        List<Shift> startList = new ArrayList<>();
        List<Shift> endList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

        Date day = null;
        try {
            day = sdf.parse("01072020");
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

            shiftList.add(morningShift);
            shiftList.add(eveningShift);

        }

        return shiftList;
    }

    public List<Room> getRoomList() {
        return mRoomList;
    }

    public List<Equipment> getEquipmentList() {
        return mEquipmentList;
    }

    public List<Person> getPersonList() {
        return mPersonList;
    }

    public List<Task> getTaskList() {
        return mTaskList;
    }

    public List<PiGroup> getPiGroupList() {
        return mPiGroupList;
    }

    public List<Shift> getShiftList() {
        return mShiftList;
    }

    public int getTotalCapacity() {
        return mTotalCapacity;
    }
}
