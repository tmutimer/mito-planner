import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the input data for the problem
 */
public class ProblemData {
    // TODO figure out whether to implement this as HashSet,
    //  may depend how much comparisons are used during planning
    private List<Room> mRoomList;
    private List<Equipment> mEquipmentList;
    private List<Person> mPersonList;

    public ProblemData() {
        mRoomList = getRoomList();
        mEquipmentList = getEquipmentList();
        mPersonList = getPersonList();
    }

    // Room //
    public List<Room> getRoomList() {
        List<Room> roomList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("rooms.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int capacity = Integer.parseInt(data[3]);
                    Room room = new Room(id, name, capacity);
                    roomList.add(room);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return roomList;
    }

    // Equipment //
    public List<Equipment> getEquipmentList() {
        List<Equipment> equipmentList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("equipment.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    int quantity = Integer.parseInt(data[4]);
                    Equipment equipment = new Equipment(id, name, quantity);
                    equipmentList.add(equipment);
                }
                csvReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return equipmentList;
    }

    // Person // TODO Fix this hot garbage
    public List<Person> getPersonList() throws Exception {
        List<Person> personList = new ArrayList<>();
        BufferedReader csvReader;
        {
            try {
                String row;
                csvReader = new BufferedReader(new FileReader("people.csv"));
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");
                    int id = Integer.parseInt(data[0]);
                    String name = data[1];
                    String officeName = data[4];
                    Room office = null;
                    int weeklyShiftLimit = Integer.parseInt(data[5]);
                    for (Room r : mRoomList){
                        if (officeName == r.getRoomName()) {
                            office = r;
                        }
                    }

                    // Validate that office was found:
                    if (Objects.isNull(office)) {
                        throw new Exception("Office not found! Was the name typed correctly?");
                    }

                    int quantity = Integer.parseInt(data[4]);
                    Person person = new Person(id, name, office, weeklyShiftLimit);
                    personList.add(person);
            }
        } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
