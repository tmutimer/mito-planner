package model;

import java.util.*;


/**
 * Problem fact class. Tasks include all their resource requirements, including the model.Person.
 */
public class Task {
    private final int mId;
    private final Person mPerson;
    private final String mName;
    private final Date mDueDate;
    private final List<Room> mRequiredRooms;
    private final LinkedHashMap<Equipment, Integer> mRequiredEquipment;
    private final int mPriority;

    public Task(int id, Person person, String name, Date dueDate, List<Room> rooms, LinkedHashMap<Equipment, Integer> equipment, int priority) {
        mId = id;
        mPerson = person;
        mName = name;
        mDueDate = dueDate;
        mRequiredRooms = rooms;
        mRequiredEquipment = equipment;
        mPriority = priority;
    }

    public int getId() {
        return mId;
    }

    public Person getPerson() {
        return mPerson;
    }


    public List<Room> getAllRequiredRooms() {
        /*
          Returns the office associated with the model.Person, as well as the rooms specific to the task
         */
        List<Room> roomList = new ArrayList<>();
        roomList.add(mPerson.getOffice());
        roomList.addAll(mRequiredRooms);
        return roomList;
    }

    public LinkedHashMap<Equipment, Integer> getRequiredEquipment() {
        return mRequiredEquipment;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public int getPriority() {
        return mPriority;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return  "Task name= " + mName +
                ", Person=" + mPerson +
                ", Due Date=" + mDueDate +
                ", Priority=" + mPriority;
    }
}
