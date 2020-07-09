package model;

/**
 * Each instance represents a type of equipment in a given room
 */
public class Equipment {
    private final int mId;

    // Example name: 'PCR (047)'.
    private final String mName;

    // Number of pieces of equipment of the type (and model.Room associated).
    private final int mNumberOfEquipment;

    public Equipment(int id, String name, int numberOfEquipment) {
        mId = id;
        mName = name;
        mNumberOfEquipment = numberOfEquipment;
    }


    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getNumberOfEquipment() {
        return mNumberOfEquipment;
    }

    @Override
    public String toString() {
        return "Equipment: " + mName;
    }

}
