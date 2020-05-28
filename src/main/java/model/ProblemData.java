package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    String officeName = data[4];
                    Room office = null;
                    int weeklyShiftLimit = Integer.parseInt(data[5]);
                    for (Room r : mRoomList) {
                        if (officeName.equals(r.getRoomName())) {
                            office = r;
                        }
                    }

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
        List<Person> taskList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("tasks.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String officeName = data[4];
                    Room office = null;
                    int weeklyShiftLimit = Integer.parseInt(data[5]);
                    for (Room r : mRoomList) {
                        if (officeName.equals(r.getRoomName())) {
                            office = r;
                        }
                    }

                    // Validate that office was found:
                    if (Objects.isNull(office)) {
                        throw new Exception("Office not found! Was the name typed correctly?");
                    }

                    int quantity = Integer.parseInt(data[4]);
                    Task task = new Task(id, name, office, weeklyShiftLimit);
                    taskList.add(person);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskList;

    }
}
