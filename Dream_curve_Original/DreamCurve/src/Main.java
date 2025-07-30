import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Step 1: Create a CareerMap for a 12th-grade student
        int studentID = 2022503517;
        String goal = "IPS Officer";
        CareerMap careerMap = new CareerMap(2022503517, studentID, "Path to becoming an IPS Officer");

        // Step 2: Add junction for completing a degree in History
        Junction degreeJunction = new Junction(1, "Complete Degree in History",
                "Finish undergraduate degree in History from a reputed college",
                "Education", new Date());
        careerMap.addJunction(degreeJunction);

        // Step 3: Add junctions for three stages of UPSC exam
        Junction stage1 = new Junction(2, "UPSC Prelims",
                "Study for and pass the UPSC Prelims exam",
                "Exam", new Date());
        careerMap.addJunction(stage1);

        Junction stage2 = new Junction(3, "UPSC Mains",
                "Study for and pass the UPSC Mains exam",
                "Exam", new Date());
        careerMap.addJunction(stage2);

        Junction stage3 = new Junction(4, "UPSC Interview",
                "Prepare for and succeed in the UPSC interview",
                "Exam", new Date());
        careerMap.addJunction(stage3);

        // Step 4: Add the final junction for becoming an IPS Officer
        Junction finalStage = new Junction(5, "Become IPS Officer",
                "Complete the required training and join the Indian Police Service",
                "Career", new Date());
        careerMap.addJunction(finalStage);

        // Display the Career Map for the student
        System.out.println("---- Career Map for " + goal + " ----");
        careerMap.displayMap();

        // Step 5: Create a ProgressTracker for the student
        ProgressTracker tracker = new ProgressTracker(2022503517, studentID, careerMap.getMapID(), "UPSC Prelims", 0.0);

        // Update progress through each stage
        System.out.println("\n---- Tracking Progress ----");

        tracker.updateProgress(33.33); // Completed UPSC Prelims
        System.out.println("Completed: " + stage1.getName());
        tracker.updateCurrentStage("UPSC Mains");
        tracker.recordUpdate(new Date());
        tracker.addNotes("Need to focus more on essay writing for Mains");

        tracker.updateProgress(66.66); // Completed UPSC Mains
        System.out.println("Completed: " + stage2.getName());
        tracker.updateCurrentStage("UPSC Interview");
        tracker.recordUpdate(new Date());
        tracker.addNotes("Practice mock interviews");

        tracker.updateProgress(100.00); // Completed UPSC Interview and became IPS Officer
        System.out.println("Completed: " + stage3.getName());
        tracker.updateCurrentStage("IPS Officer");
        tracker.recordUpdate(new Date());
        tracker.addNotes("Successfully joined IPS and began training");

        // Display the progress
        tracker.displayTracker();
        System.out.println("Final Junction Completed: " + finalStage.getName());
    }
}
