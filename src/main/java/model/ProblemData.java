package model;

import org.drools.core.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final List<TimeGrain> mTimeGrainList;
    private final List<TaskAssignment> mTaskAssignmentList;

    public ProblemData() throws Exception {
        // TODO make total capacity dynamic from the appropriate csv - maybe there should be a settings.txt file.
        mTotalCapacity = 50;
        mRoomList = createRoomList();
        mPiGroupList = createPiGroupList();
        mEquipmentList = createEquipmentList();
        mPersonList = createPersonList();
        mShiftList = createShiftList();
        mTimeGrainList = createTimeGrainList();
        mTaskList = generateTaskList(200);
        mTaskAssignmentList = createTaskAssignmentList();
    }

    private List<TaskAssignment> createTaskAssignmentList() {
        List<TaskAssignment> taskAssignments = new ArrayList<>();
        for (Task t : mTaskList) {
            taskAssignments.add(new TaskAssignment(t));
        }
        return taskAssignments;
    }

    private List<TimeGrain> createTimeGrainList() {
        List<TimeGrain> slotList = new ArrayList<>();
        for (Shift s : mShiftList) {
            slotList.addAll(TimeGrain.fromShift(s));
        }
        return slotList;
    }

    public List<TimeGrain> getTimeGrainList() {
        return mTimeGrainList;
    }

    // WORKING ON THIS TO GENERATE LARGE TASK LISTS
    public List<Task> generateTaskList(int numTasks) {
        Random random = new Random();
        int id_counter = 0;
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < numTasks ; i++) {
            int id = ++id_counter;
            Person person = mPersonList.get(random.nextInt(mPersonList.size()));
            String name = "Random Task #X".replace("X", String.valueOf(id));
            int duration = random.nextInt(11)+1;
            List<Equipment> equipmentUsage = new ArrayList<>();
            equipmentUsage.add(mEquipmentList.get(random.nextInt(mEquipmentList.size())));
            //TODO implement due date, precedingtask, and immediately_follows_preceding_task

            Task precedingTask = null;
            List<Task> precedingTaskOptions = new ArrayList<>();
            // 40% of the time
            if (random.nextInt(10) >= 6) {
                // filter the task list for those which do not already precede another
                for (Task t : taskList) {
                    if (t.getPerson() == person) {
                        boolean alreadyUsed = false;
                        for (Task t2 : taskList) {
                            if (Objects.nonNull(t2.getPrecedingTaskId())) {
                                if (t2.getPrecedingTaskId() == t.getId()) {
                                    alreadyUsed = true;
                                    break;
                                }
                            }
                        }
                        // if no other task is using this task as a preceding task, then it's a possibility
                        if (!alreadyUsed) {
                            precedingTaskOptions.add(t);
                        }
                    }
                }
                //using Task instead of Id for later use in this method
                if (precedingTaskOptions.size() > 0) {
                    precedingTask = precedingTaskOptions.get(random.nextInt(precedingTaskOptions.size()));
                }
            }

            LocalDateTime dueDate = null;
            List<LocalDateTime>dateOptions = new ArrayList<>();
            for(Shift s : mShiftList) {
                dateOptions.add(s.getStartTime());
            }

//            30% of the time set a due date
            if (random.nextInt(10) >= 7) {
                if (Objects.isNull(precedingTask)) dueDate = dateOptions.get(random.nextInt(dateOptions.size()));
                else if (Objects.nonNull(precedingTask.getDueDate())) {
                    ArrayList<LocalDateTime> dateOptionsConstrained = new ArrayList<>();
                    for (LocalDateTime d : dateOptions) {
                        if (d.isAfter(precedingTask.getDueDate())) {
                            dateOptionsConstrained.add(d);
                        }
                    }
                    if(dateOptionsConstrained.size() > 0) dueDate = dateOptionsConstrained.get(random.nextInt(dateOptionsConstrained.size()));
                }
            }

            boolean immediatelyFollowsPrecedingTask = random.nextInt(10) >= 4;
            int priority = random.nextInt(10) + 1;

            Integer precedingTaskId = null;
            if (Objects.nonNull(precedingTask)) precedingTaskId = precedingTask.getId();

            Task task = new Task(id, precedingTaskId, immediatelyFollowsPrecedingTask,
                    person, name, duration,
                    dueDate, determineRoomUsage(person, equipmentUsage),
                    equipmentUsage, priority);
            taskList.add(task);
        }
        return taskList;
    }

    public List<Room> determineRoomUsage(Person person, List<Equipment> equipment) {
        List<Room> rooms = new ArrayList<>();
        for(Equipment e : equipment) {
            rooms.add(e.getRoom());
        }
        rooms.add(person.getOffice());

        return rooms;
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
//                    System.out.println("Loading in room with name: " + name);
                    int capacity = Integer.parseInt(data[3]);
                    Room room = new Room(id, name, capacity);
                    roomList.add(room);
                }
//                for (Room r : ) {
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
    public List<Equipment> createEquipmentList() throws Exception {
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
                    String roomName = data[3];
                    Room room = null;
//                    System.out.println("Trying to match: " + roomName);
                    for (Room r : mRoomList) {
//                        System.out.println(r.getRoomName() + " = " + roomName + "?");
                        if (r.getRoomName().equals(roomName)) {
                            room = r;
                        }
                    }

                    if (room == null && !StringUtils.isEmpty(roomName)) {
                        System.out.println("Failed to match " + roomName);
                        throw new Exception("Room not found for '" + roomName + "'! Was the name typed correctly?");
                    }

                    int quantity = Integer.parseInt(data[4]);
                    Equipment equipment = new Equipment(id, name, room, quantity);
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

    public List<Task> createTaskList() throws DateTimeParseException {
        List<Task> taskList = new ArrayList<>();
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
                    for (Person p : mPersonList) {
                        if (p.getName().equals(personString)) {
                            person = p;
                        }
                    }
                    String name = data[2];
                    int priority = Integer.parseInt(data[6]);
                    LocalDateTime dueDate = null;
                    if (!StringUtils.isEmpty(data[5])) {
                        dueDate = LocalDate.parse(data[5], DateTimeFormatter.ofPattern("ddMMuuuu")).atStartOfDay();
                    }

                    int duration = Integer.parseInt(data[3]);

                    Integer precedingTaskId = null;
                    String precedingTaskString = data[7];
                    if (!StringUtils.isEmpty(data[7])) {
                        precedingTaskId = Integer.parseInt(data[7]);
                    }
                    // TODO get equipment reading from tasks.csv
                    Task task = new Task(id, precedingTaskId, false, person, name, duration, dueDate, null, null, priority);
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

            Shift morningShift = new Shift(morningStart.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), morningEnd.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            morningStart.add(Calendar.DAY_OF_YEAR, 1);
            morningEnd.add(Calendar.DAY_OF_YEAR, 1);

            Shift eveningShift = new Shift(eveningStart.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), eveningEnd.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
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

    public List<TaskAssignment> getTaskAssignmentList() {
        return mTaskAssignmentList;
    }
}
