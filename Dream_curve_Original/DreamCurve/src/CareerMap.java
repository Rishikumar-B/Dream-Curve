import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CareerMap {
    private int mapID;
    private int studentID;
    private String name;
    private List<Junction> junctions;
    private Date createdDate;
    private Date reviewedDate;
    private String status;

    // Constructor
    public CareerMap(int mapID, int studentID, String name) {
        this.mapID = mapID;
        this.studentID = studentID;
        this.name = name;
        this.junctions = new ArrayList<>();
        this.createdDate = new Date();
        this.status = "Pending Review";
    }

    public int getMapID() {
        return this.mapID;
    }

    // Add a junction to the map
    public void addJunction(Junction junction) {
        if (junction != null) {
            this.junctions.add(junction);
            System.out.println("Junction added: " + junction.getName());
        }
    }

    // Remove a junction from the map
    public void removeJunction(Junction junction) {
        if (this.junctions.contains(junction)) {
            this.junctions.remove(junction);
            System.out.println("Junction removed: " + junction.getName());
        }
    }

    // Update the status of the map
    public void updateStatus(String newStatus) {
        if (newStatus != null && !newStatus.isEmpty()) {
            this.status = newStatus;
            System.out.println("Status updated to: " + newStatus);
        }
    }

    // Review the map
    public void reviewMap(Date date) {
        this.reviewedDate = date;
        System.out.println("Map reviewed on: " + date.toString());
    }

    // Display the career map details
    public void displayMap() {
        System.out.println("Career Map for Student ID: " + studentID);
        System.out.println("Map Name: " + name);
        System.out.println("Status: " + status);
        System.out.println("Junctions: ");
        for (Junction junction : junctions) {
            System.out.println(" - " + junction.getName());
        }
    }
}
