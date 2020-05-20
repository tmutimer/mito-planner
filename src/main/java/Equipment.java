/**
 * Each instance represents a type of equipment in a given room
 */
public class Equipment {
    private int[] mId;

    // Example name: 'PCR (047)'.
    private String mName;

    // Not currently necessary for solving, but may be useful if approach is changed.
    private Room mRoom;

    // Number of pieces of equipment of the type (and Room associated).
    private int mNumberOfEquipment;


    public int[] getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getNumberOfEquipment() {
        return mNumberOfEquipment;
    }
}
