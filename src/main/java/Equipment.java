/**
 * Each instance represents a type of equipment in a given room
 */
public class Equipment {
    private int[] mId;
    private String mName;
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
