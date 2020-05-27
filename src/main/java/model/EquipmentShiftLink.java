package model;

public class EquipmentShiftLink {
    private Shift mShift;
    private Equipment mEquipment;
    private int mUsage;

    public Shift getShift() {
        return mShift;
    }

    public Equipment getEquipment() {
        return mEquipment;
    }

    public int getUsage() {
        return mUsage;
    }

    public void setUsage(int usage) {
        mUsage = usage;
    }

    public boolean isEquipmentOverCapacity(){
        // TODO this implementation will need to be updated when equipment is used per minute, not per shift.
        return mUsage > mEquipment.getNumberOfEquipment();
    }
}
