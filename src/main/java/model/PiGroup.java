package model;

/**
 * Problem fact. People belong to PI Groups.
 */
public class PiGroup {
    private final String mName;
    private final int mId;
    private static int sIdCounter = 0;

    public PiGroup(String name) {
        mId = ++sIdCounter;
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
