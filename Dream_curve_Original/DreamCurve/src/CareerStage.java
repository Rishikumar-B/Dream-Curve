import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CareerStage {
    private int stageID;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<String> associatedCourses;

    // Constructor
    public CareerStage(int stageID, String name, String description, Date startDate, Date endDate) {
        this.stageID = stageID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.associatedCourses = new ArrayList<>();
    }

    // Update the name of the career stage
    public void updateName(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
            System.out.println("Stage name updated to: " + newName);
        }
    }

    // Update the description of the career stage
    public void updateDescription(String newDescription) {
        if (newDescription != null && !newDescription.isEmpty()) {
            this.description = newDescription;
            System.out.println("Stage description updated.");
        }
    }

    // Update the start and end dates
    public void updateDates(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;
        System.out.println("Stage dates updated.");
    }

    // Add a course to the stage
    public void addCourse(String course) {
        if (course != null && !course.isEmpty()) {
            this.associatedCourses.add(course);
            System.out.println("Course added: " + course);
        }
    }

    // Remove a course from the stage
    public void removeCourse(String course) {
        if (this.associatedCourses.contains(course)) {
            this.associatedCourses.remove(course);
            System.out.println("Course removed: " + course);
        }
    }

    // Display stage details
    public void displayStage() {
        System.out.println("Career Stage: " + name);
        System.out.println("Description: " + description);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Courses: " + associatedCourses);
    }
}
