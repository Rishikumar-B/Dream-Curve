import java.util.Date;

public class Junction {
    private int junctionID;
    private String name;
    private String description;
    private String type;
    private Date date;

    // Constructor
    public Junction(int junctionID, String name, String description, String type, Date date) {
        this.junctionID = junctionID;
        this.name = name;
        this.description = description;
        this.type = type;
        this.date = date;
    }

    // Update junction name
    public void updateName(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
            System.out.println("Junction name updated to: " + newName);
        }
    }

    // Update junction description
    public void updateDescription(String newDescription) {
        if (newDescription != null && !newDescription.isEmpty()) {
            this.description = newDescription;
            System.out.println("Junction description updated.");
        }
    }

    // Other methods...

    // Getters
    public String getName() {
        return name;
    }
}
