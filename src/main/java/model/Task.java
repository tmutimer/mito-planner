package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Problem fact class. Tasks include all their resource requirements, including the model.Person.
 */
public class Task {
    private final int mId;
    private final Integer mPrecedingTaskId;
    private final boolean mImmediatelyFollowsPrecedingTask;
    private final Person mPerson;
    private final String mName;
    private final int mDuration;
    private final LocalDateTime mDueDate;
    private final List<Room> mRequiredRooms;
    private final List<Equipment> mRequiredEquipment;
    private final int mPriority;

    static final int EQUIPMENT_TIME_DIFFICULTY_WEIGHT = 1;
    static final int EQUIPMENT_TYPE_DIFFICULTY_WEIGHT = 1;
    static final int DUE_DATE_DIFFICULTY_WEIGHT = 2000;

    public Task(int id, Integer precedingTaskId,  boolean immediatelyFollowsPrecedingTask, Person person, String name, int duration, LocalDateTime dueDate, List<Room> rooms, List<Equipment> equipment, int priority) {
        mId = id;
        mDuration = duration;
        mImmediatelyFollowsPrecedingTask = immediatelyFollowsPrecedingTask;
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

    public int getDuration() {
        return mDuration;
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

    public List<Equipment> getRequiredEquipment() {
        assert !Objects.isNull(mRequiredEquipment);
        return mRequiredEquipment;
    }

    public LocalDateTime getDueDate() {
        return mDueDate;
    }

    public int getPriority() {
        return mPriority;
    }

    public String getName() {
        return mName;
    }

    public List<Room> getRequiredRooms() {
        return mRequiredRooms;
    }

    public Integer getPrecedingTaskId() {
        return mPrecedingTaskId;
    }

    public boolean isImmediatelyFollowsPrecedingTask() {
        return mImmediatelyFollowsPrecedingTask;
    }

    @Override
    public String toString() {
        return  "Task name=" + mName +
                ", Person=" + mPerson +
                ", Due Date=" + mDueDate +
                ", Priority=" + mPriority +
                ", Preceding Task=" + mPrecedingTaskId;
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
    public static int getDifficulty(Task t) {
        // TODO make these final static members in the Task class
        int difficulty = 0;
        List<Equipment> equipment = t.getRequiredEquipment();

        int typesOfEquipment = equipment.size();
        difficulty += t.getDuration();
        int daysUntilDue = (int) ChronoUnit.DAYS.between(LocalDate.now(), t.getDueDate());

        //minimum value is zero so that weird things won't happen when due date in the past
        if (daysUntilDue < 0) {
            daysUntilDue = 0;
        }

        difficulty += typesOfEquipment * EQUIPMENT_TYPE_DIFFICULTY_WEIGHT;
        difficulty += daysUntilDue * DUE_DATE_DIFFICULTY_WEIGHT;
        return difficulty;
    }

    public static int getIdStatic(Task t) {
        return t.getId();
    }

    public boolean hasDueDate() {
        return Objects.nonNull(mDueDate);
    }
}
