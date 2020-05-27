package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Problem fact class. Tasks include all their resource requirements, including the model.Person.
 */
public class Task {
    private Person mPerson;

    private Date mDueDate;

    private final List<Room> mOccupiedLabs;

    public Task(List<Room> rooms) {
        mOccupiedLabs = rooms;
    }

    public Person getPerson() {
        return mPerson;
    }

    public List<Room> getOccupiedRooms() {
        /**
         * Returns the office associated with the model.Person, as well as the rooms specific to the task
         */
        List<Room> roomList = new ArrayList<Room>();
        roomList.add(mPerson.getOffice());
        roomList.addAll(mOccupiedLabs);
        return roomList;
    }

    public Date getDueDate() {
        return mDueDate;
    }
}
