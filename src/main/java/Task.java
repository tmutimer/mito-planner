import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Problem fact class. Tasks include all their resource requirements, including the Person.
 */
public class Task {
    private Person mPerson;

    // Initialising as null here unless I decide to do so in Constructor
    private List<Room> mOccupiedLabs;

    public Task(List<Room> rooms) {
        mOccupiedLabs = rooms;
    }

    public Person getPerson() {
        return mPerson;
    }

    public List<Room> getOccupiedRooms() {
        /**
         * Returns the office associated with the Person, as well as the rooms specific to the task
         */
        List<Room> roomList = Arrays.asList(new Room[]{mPerson.getOffice()});
        if (Objects.nonNull(roomList)) {
            roomList.addAll(mOccupiedLabs);
        }
        return roomList;
    }

}
