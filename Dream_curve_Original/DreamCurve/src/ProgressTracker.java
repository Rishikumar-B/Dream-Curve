import java.util.Date;

public class ProgressTracker {
    private int trackerID;
    private int studentID;
    private int careerMapID;
    private String currentStage;
    private double progressPercentage;
    private Date lastUpdateDate;
    private String notes;

    // Constructor
    public ProgressTracker(int trackerID, int studentID, int careerMapID, String currentStage, double progressPercentage) {
        this.trackerID = trackerID;
        this.studentID = studentID;
        this.careerMapID = careerMapID;
        this.currentStage = currentStage;
        this.progressPercentage = progressPercentage;
        this.lastUpdateDate = new Date(); // Automatically set to current date
        this.notes = "";
    }

    // Update the progress percentage
    public void updateProgress(double newPercentage) {
        if (newPercentage >= 0 && newPercentage <= 100) {
            this.progressPercentage = newPercentage;
            this.lastUpdateDate = new Date(); // Record the date of the update
            System.out.println("Progress updated to: " + newPercentage + "%");
        } else {
            System.out.println("Invalid progress percentage.");
        }
    }

    // Update the current stage
    public void updateCurrentStage(String newStage) {
        if (newStage != null && !newStage.isEmpty()) {
            this.currentStage = newStage;
            this.lastUpdateDate = new Date();
            System.out.println("Current stage updated to: " + newStage);
        } else {
            System.out.println("Invalid stage.");
        }
    }

    // Add notes to the tracker
    public void addNotes(String newNotes) {
        if (newNotes != null && !newNotes.isEmpty()) {
            this.notes += newNotes + "\n";
            this.lastUpdateDate = new Date();
            System.out.println("Notes added.");
        } else {
            System.out.println("Invalid notes.");
        }
    }

    // Record the last update date
    public void recordUpdate(Date date) {
        this.lastUpdateDate = date;
        System.out.println("Update recorded on: " + date.toString());
    }

    // Display tracker details
    public void displayTracker() {
        System.out.println("Progress Tracker for Student ID: " + studentID);
        System.out.println("Current Stage: " + currentStage);
        System.out.println("Progress Percentage: " + progressPercentage + "%");
        System.out.println("Last Update Date: " + lastUpdateDate);
        System.out.println("Notes: " + notes);
    }
}
