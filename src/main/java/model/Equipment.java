package model;

/**
 * Each instance represents a type of equipment in a given room
 */
public class Equipment {
    private final int mId;

    // Example name: 'PCR (047)'.
    private final String mName;
    private final Room mRoom;

    // Number of pieces of equipment of the type (and model.Room associated).
    // TODO this might need to change depending on how input data is collected.
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
