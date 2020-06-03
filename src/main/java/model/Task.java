package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Problem fact class. Tasks include all their resource requirements, including the model.Person.
 */
public class Task {
    private final int mId;
    private final Person mPerson;
    private final String mName;
    private final Date mDueDate;
    private final List<Room> mOccupiedLabs;
    private final int mPriority;

    public Task(int id, Person person, String name, Date dueDate, List<Room> rooms, int priority) {
        mId = id;
        mPerson = person;
        mName = name;
        mDueDate = dueDate;
        mOccupiedLabs = rooms;
        mPriority = priority;
    }

    public Person getPerson() {
        return mPerson;
    }


    public List<Room> getOccupiedRooms() {
        /*
          Returns the office associated with the model.Person, as well as the rooms specific to the task
         */
        List<Room> roomList = new ArrayList<>();
        roomList.add(mPerson.getOffice());
        roomList.addAll(mOccupiedLabs);
        return roomList;
    }

    public Date getDueDate() {
        return mDueDate;
    }

    public List<Room> getOccupiedLabs() {
        return mOccupiedLabs;
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
