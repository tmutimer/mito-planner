import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the input data for the problem
 */
public class ProblemData {

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
    }
}

    // Person // TODO Fix this hot garbage
    public List<Person> getPersonList() {
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
                    int quantity = Integer.parseInt(data[4]);
                    Person person = new Person();
                    personList.add(person);
            }
        } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
