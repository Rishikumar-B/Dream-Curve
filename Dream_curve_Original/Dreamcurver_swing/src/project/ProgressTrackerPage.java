package project;  // Specify the package here

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

public class ProgressTrackerPage extends JFrame {
    public ProgressTrackerPage(String username) {
        setTitle("Progress Tracker");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a button to open the HTML page for progress tracker
        JButton openButton = new JButton("Open Progress Tracker");
        openButton.setFont(new Font("Arial", Font.BOLD, 16));
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openProgressTrackerHTML();
            }
        });
        add(openButton, BorderLayout.CENTER);
        setVisible(true);
    }

    private void openProgressTrackerHTML() {
        // Generate the HTML page dynamically or statically
        String htmlContent = generateProgressTrackerHTML();

        // Save the HTML content to a file
        try {
            FileWriter fileWriter = new FileWriter("ProgressTracker.html");
            fileWriter.write(htmlContent);
            fileWriter.close();

            // Open the generated HTML file in the default web browser
            Desktop.getDesktop().browse(new File("ProgressTracker.html").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateProgressTrackerHTML() {
        // CSS Styling and Animation
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><head>");

        // Add CSS for styling the page and the circular progress bar
        htmlContent.append("<style>")
                .append("body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; }")
                .append("h1 { text-align: center; color: #4CAF50; margin-top: 20px; }")
                .append("table { width: 90%; margin: 20px auto; border-collapse: collapse; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }")
                .append("th, td { padding: 12px; text-align: center; border: 1px solid #ddd; }")
                .append("th { background-color: #4CAF50; color: white; }")
                .append("td { background-color: #f9f9f9; }")
                .append("tr:nth-child(even) td { background-color: #f1f1f1; }")
                .append("tr:hover { background-color: #f1f1f1; cursor: pointer; }")
                .append(".status-completed { background-color: #8bc34a; color: white; }")
                .append(".status-in-progress { background-color: #ff9800; color: white; }")
                .append(".completion { text-align: center; padding: 10px 0; background-color: #2196F3; color: white; font-weight: bold; }")

                // Circular Progress Bar CSS
                .append("@keyframes progress { 0% { --percentage: 0; } 100% { --percentage: var(--value); } }")
                .append("@property --percentage { syntax: '<number>'; inherits: true; initial-value: 0; }")
                .append("[role='progressbar'] {")
                .append("  --percentage: var(--value);")
                .append("  --primary: #369;")
                .append("  --secondary: #adf;")
                .append("  --size: 100px;")
                .append("  animation: progress 2s 0.5s forwards;")
                .append("  width: var(--size);")
                .append("  aspect-ratio: 1;")
                .append("  border-radius: 50%;")
                .append("  position: relative;")
                .append("  overflow: hidden;")
                .append("  display: grid;")
                .append("  place-items: center;")
                .append("}")
                .append("[role='progressbar']::before {")
                .append("  content: '';")
                .append("  position: absolute;")
                .append("  top: 0;")
                .append("  left: 0;")
                .append("  width: 100%;")
                .append("  height: 100%;")
                .append("  background: conic-gradient(var(--primary) calc(var(--percentage) * 1%), var(--secondary) 0);")
                .append("  mask: radial-gradient(white 55%, transparent 0);")
                .append("  mask-mode: alpha;")
                .append("  -webkit-mask: radial-gradient(#0000 55%, #000 0);")
                .append("  -webkit-mask-mode: alpha;")
                .append("}")
                .append("[role='progressbar']::after {")
                .append("  counter-reset: percentage var(--value);")
                .append("  content: counter(percentage) '%';")
                .append("  font-family: Helvetica, Arial, sans-serif;")
                .append("  font-size: calc(var(--size) / 5);")
                .append("  color: var(--primary);")
                .append("}")
                .append("body { margin: 0; display: grid; place-items: center; height: 100vh; background: #f0f8ff; }")
                .append("</style>")
                .append("</head><body>");

        htmlContent.append("<h1>Progress Tracker</h1>");
        htmlContent.append("<table class='fade-in'><tr><th>Map Name</th><th>Junction</th><th>Stage Notes</th><th>Status</th><th>Completion Percentage</th></tr>");

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DreamCurve", "postgres", "root")) {
            // Query to fetch career map, junction, and stage details
            String query = "SELECT cm.map_name, j.junction_name, s.stage_id, s.notes AS stage_notes, s.has_completed, s.map_id " +
                    "FROM CareerMap_table cm " +
                    "JOIN Junction_table j ON cm.map_id = j.map_id " +
                    "JOIN Stages_table s ON j.junction_id = s.junction_id " +
                    "ORDER BY cm.user_name, cm.map_name, j.create_date, s.start_date";

            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Process each result
            while (rs.next()) {
                String mapName = rs.getString("map_name");
                String junctionName = rs.getString("junction_name");
                String stageNotes = rs.getString("stage_notes");
                boolean isCompleted = rs.getBoolean("has_completed");
                int mapId = rs.getInt("map_id");

                // Generate HTML table row for each stage
                htmlContent.append("<tr>")
                        .append("<td>").append(mapName).append("</td>")
                        .append("<td>").append(junctionName).append("</td>")
                        .append("<td>").append(stageNotes).append("</td>")
                        .append("<td class='").append(isCompleted ? "status-completed" : "status-in-progress").append("'>")
                        .append(isCompleted ? "Completed" : "In Progress")
                        .append("</td>")
                        .append("<td>");

                // Calculate the completion percentage for the map
                int totalStages = 0;
                int completedStages = 0;

                // Query to get the total stages and completed stages for the specific map_id
                String progressQuery = "SELECT COUNT(*) AS total_stages, SUM(CASE WHEN has_completed THEN 1 ELSE 0 END) AS completed_stages " +
                        "FROM Stages_table s " +
                        "JOIN Junction_table j ON s.junction_id = j.junction_id " +
                        "WHERE j.map_id = ?";  // Use map_id to filter stages

                PreparedStatement progressStmt = conn.prepareStatement(progressQuery);
                progressStmt.setInt(1, mapId);
                ResultSet progressRs = progressStmt.executeQuery();

                if (progressRs.next()) {
                    totalStages = progressRs.getInt("total_stages");
                    completedStages = progressRs.getInt("completed_stages");
                }

                // Calculate completion percentage
                double completionPercentage = (totalStages > 0) ? ((double) completedStages / totalStages) * 100 : 0;

                // Add progress bar and completion percentage to the HTML content
                htmlContent.append("<div role='progressbar' style='--value: " + (int) completionPercentage + ";'></div>");
                htmlContent.append("</td></tr>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        htmlContent.append("</table></body></html>");
        return htmlContent.toString();
    }
}
