import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Shift Assignment objects will be assigned to Persons during Planning.
 * There are as many ShiftAssignment instances per Shift instance as the Floor capacity.
 */
@PlanningEntity
public class ShiftAssignment {
    private Person mPerson;
    private Shift mShift;

    // The PlanningVariable annotation shares "taskList" with the
    // annotation of the function providing the list of Tasks
    @PlanningVariable(valueRangeProviderRefs = {"taskList"})
    public Person getPerson() {
        return mPerson;
    }

    public void setPerson(Person person) {
        mPerson = person;
    }
}
