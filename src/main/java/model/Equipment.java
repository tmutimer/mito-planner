package model;

/**
 * A resource like a PCR machine, or an Office Desk.
 */
public class Equipment {
    private final int mId;

    // Example name: 'PCR (047)'.
    private final String mName;
    private final Room mRoom;

    // Number of pieces of equipment of the type (and model.Room associated).
    // TODO Figure out how to deal with multiple of the same equipment in the same room
    //  Should they just be separate instances? How to distinguish/select in planning?
    private final int mNumberOfEquipment;

    public Equipment(int id, String name, Room room, int numberOfEquipment) {
        mId = id;
        mRoom = room;
        mName = name;
        mNumberOfEquipment = numberOfEquipment;
    }


    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Room getRoom() {
        return mRoom;
    }

    public int getNumberOfEquipment() {
        return mNumberOfEquipment;
    }

    @Override
    public String toString() {
        return "Equipment: " + mName;
    }

}
