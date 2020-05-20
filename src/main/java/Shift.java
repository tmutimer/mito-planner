/**
 * The Shift is a half-day slot. This has a fixed relationship to ShiftAssignment objects,
 * which does not change during planning. The shift handles room and equipment usage.
 */
public class Shift {
    public final int[] mRoomIdList;

    public Shift(int[] roomIdList) {
        mRoomIdList = roomIdList;
    }

}
