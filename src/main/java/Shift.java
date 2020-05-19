import java.util.HashMap;
import java.util.List;

/**
 * The Shift is a half-day slot. This has a fixed relationship to ShiftAssignment objects,
 * which does not change during planning. The shift handles room and equipment usage.
 */
public class Shift {
    public int[] mRoomIdList;

    public Shift(int[] roomIdList) {
        mRoomIdList = roomIdList;
    }

}
