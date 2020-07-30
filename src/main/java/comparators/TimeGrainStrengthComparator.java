package comparators;

import model.TimeGrain;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares the planning strength of TimeGrains
 */
public class TimeGrainStrengthComparator implements Comparator<TimeGrain>, Serializable {

    private static final Comparator<TimeGrain> COMPARATOR = Comparator.comparingInt(TimeGrain::getStrength)
            .thenComparingInt(TimeGrain::getId);
    @Override
    public int compare(TimeGrain tg1, TimeGrain tg2) {
        return COMPARATOR.compare(tg1, tg2);
    }
}
