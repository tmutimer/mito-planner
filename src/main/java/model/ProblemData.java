package model;

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
    private List<Room> mRoomList;
    private List<Equipment> mEquipmentList;
    private List<Person> mPersonList;
    private List<Task> mTaskList;
    private List<PiGroup> mPiGroupList;

    public ProblemData() {
        mRoomList = getRoomList();
        mEquipmentList = getEquipmentList();

        try {
            mPersonList = getPersonList();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // TODO add validations as per model.Person
    // model.Room //
    public List<Room> getRoomList() {
        List<Room> roomList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("rooms.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int capacity = Integer.parseInt(data[3]);
                    Room room = new Room(id, name, capacity);
                    roomList.add(room);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return roomList;
    }

    // TODO see if any validations are needed
    // model.Equipment //
    public List<Equipment> getEquipmentList() {
        List<Equipment> equipmentList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("equipment.csv"));
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

    public List<PiGroup> getPiGroupList() {
        List<PiGroup> piGroupList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("pi_groups.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int quantity = Integer.parseInt(data[4]);
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

    // model.Person // TODO Fix this hot garbage
    public List<Person> getPersonList() throws Exception {
        List<Person> personList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("people.csv"));
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
                        if (officeName.equals(r.getRoomName())) {
                            office = r;
                        }
                    }

                    int weeklyShiftLimit = Integer.parseInt(data[5]);

                    // Validate that office was found:
                    if (Objects.isNull(office)) {
                        throw new Exception("Office not found! Was the name typed correctly?");
                    }

                    int quantity = Integer.parseInt(data[4]);
                    Person person = new Person(id, name, office, piGroup, weeklyShiftLimit);
                    personList.add(person);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return personList;
    }

    public List<Task> getTaskList() throws Exception {
        List<Task> taskList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("tasks.csv"));
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

                    // Validate that office was found:
                    Task task = new Task(id, person, name, null, null, priority);
                    taskList.add(task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskList;

    }

    // Currently returns a list of 100 shifts, starting from the 1st of july, every day for 50 days, 7am-12pm, 1pm-6pm
    public List<Shift> getShiftList() {
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

}
