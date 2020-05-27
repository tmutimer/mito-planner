package model;

public class RoomShiftLink {
    private Shift mShift;
    private Room mRoom;
    private int mOccupancy;

    public Shift getShift() {
        return mShift;
    }

    public Room getRoom() {
        return mRoom;
    }


    public int getOccupancy() {
        return mOccupancy;
    }

    public void setOccupancy(int occupancy) {
        mOccupancy = occupancy;
    }

    public boolean isRoomOverCapacity(){
        return mOccupancy > mRoom.getCapacity();
    }
}
