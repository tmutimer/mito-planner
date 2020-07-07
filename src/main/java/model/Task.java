package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Problem fact class. Tasks include all their resource requirements, including the model.Person.
 */
public class Task {
    private final int mId;
    private final Integer mPrecedingTaskId;
    private final Person mPerson;
    private final String mName;
    private final Date mDueDate;
    private final List<Room> mRequiredRooms;
    private final LinkedHashMap<Equipment, Integer> mRequiredEquipment;
    private final int mPriority;

    static int EQUIPMENT_TIME_DIFFICULTY_WEIGHT = 1;
    static int EQUIPMENT_TYPE_DIFFICULTY_WEIGHT = 1;
    static int DUE_DATE_DIFFICULTY_WEIGHT = 1;

    public Task(int id, Integer precedingTaskId, Person person, String name, Date dueDate, List<Room> rooms, LinkedHashMap<Equipment, Integer> equipment, int priority) {
        mId = id;
        Integer mPrecedingTaskIdTemp = null;
        if (!Objects.isNull(precedingTaskId)) {
            mPrecedingTaskIdTemp = precedingTaskId;
        }
        mPrecedingTaskId = mPrecedingTaskIdTemp;
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
        assert !Objects.isNull(mRequiredEquipment);
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

    public Integer getPrecedingTaskId() {
        return mPrecedingTaskId;
    }

    public List<Room> getRequiredRooms() {
        return mRequiredRooms;
    }

    @Override
    public String toString() {
        return  "Task name=" + mName +
                ", Person=" + mPerson +
                ", Due Date=" + mDueDate +
                ", Priority=" + mPriority +
                ", PrecedingTask=" + mPrecedingTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return mId == task.mId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId);
    }

    // Static methods used in comparator. No idea why they must be static, but oh well...
    public static int getStrength(Task t) {
        // TODO make these final static members in the Task class
        int difficulty = 0;
        LinkedHashMap<Equipment, Integer> equipment = t.getRequiredEquipment();

        int totalEquipmentMinutes = 0;
        int typesOfEquipment = equipment.size();
        int daysUntilDue = 0;

        for (int time: equipment.values()) {
            totalEquipmentMinutes += time;
        }

        daysUntilDue = (int) ChronoUnit.DAYS.between(LocalDate.now(), t.getDueDate().toInstant());

        //minimum value is zero so that weird things won't happen when due date in the past
        if (daysUntilDue < 0) {
            daysUntilDue = 0;
        }

        difficulty += typesOfEquipment * EQUIPMENT_TYPE_DIFFICULTY_WEIGHT;
        difficulty += totalEquipmentMinutes * EQUIPMENT_TIME_DIFFICULTY_WEIGHT;
        difficulty += daysUntilDue * DUE_DATE_DIFFICULTY_WEIGHT;
        return difficulty;
    }

    public static int getIdStatic(Task t) {
        return t.getId();
    }
}
