package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewMap {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/DreamCurve";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewMap::new);
    }

    public ViewMap() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Career Map Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel mainPanel = new JPanel();
        frame.add(mainPanel);

        // Button to view the career map
        JButton viewMapButton = new JButton("See Career Map View");
        mainPanel.add(viewMapButton);

        // Add action listener for the "See Map View" button
        viewMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCareerMapView(frame);
            }
        });

        frame.setVisible(true);
    }

    private void showCareerMapView(JFrame frame) {
        String mapHTML = generateMapHTML();
        if (mapHTML != null) {
            displayMapInHTMLView(frame, mapHTML);
        }
    }

    private String generateMapHTML() {
        StringBuilder htmlBuilder = new StringBuilder();

        // Basic HTML structure
        htmlBuilder.append("<html><head><title>Career Map View</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; background-color: #f4f4f9; }")
                .append(".map-container { padding: 20px; }")
                .append(".junction-container { margin-bottom: 20px; }")
                .append(".junction { background-color: #007bff; color: white; padding: 10px; border-radius: 8px; text-align: center; }")
                .append(".stages { margin-left: 20px; }")
                .append(".stage { background-color: #28a745; color: white; padding: 8px; border-radius: 5px; margin-bottom: 5px; }")
                .append(".stage.completed { background-color: #6c757d; }")
                .append("</style></head><body>")
                .append("<div class='map-container'>");

        // Fetch the Career Map data
        String mapName = getCareerMapName();
        htmlBuilder.append("<h2>Career Map: ").append(mapName).append("</h2>");

        // Fetch all junctions for this map
        List<Junction> junctions = getJunctionsForMap();

        // For each junction, fetch and display its stages
        for (Junction junction : junctions) {
            htmlBuilder.append("<div class='junction-container'>")
                    .append("<div class='junction'><h3>").append(junction.getName()).append("</h3></div>")
                    .append("<div class='stages'>");

            List<Stage> stages = getStagesForJunction(junction.getJunctionId());
            for (Stage stage : stages) {
                htmlBuilder.append("<div class='stage").append(stage.isCompleted() ? " completed" : "").append("'>")
                        .append(stage.getName())
                        .append("</div>");
            }

            htmlBuilder.append("</div></div>");
        }

        htmlBuilder.append("</div></body></html>");
        return htmlBuilder.toString();
    }

    private String getCareerMapName() {
        // Get Career Map name from the database
        String query = "SELECT map_name FROM CareerMap_table LIMIT 1"; // You can adjust the query if needed
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("map_name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Unknown Map";
    }

    private List<Junction> getJunctionsForMap() {
        List<Junction> junctions = new ArrayList<>();
        String query = "SELECT junction_id, junction_name FROM junction_table WHERE map_id = 1"; // Modify map_id as needed

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                junctions.add(new Junction(rs.getInt("junction_id"), rs.getString("junction_name")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return junctions;
    }

    private List<Stage> getStagesForJunction(int junctionId) {
        List<Stage> stages = new ArrayList<>();
        String query = "SELECT stage_id, stage_name, has_completed FROM Stages_table WHERE junction_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, junctionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stages.add(new Stage(rs.getInt("stage_id"), rs.getString("stage_name"), rs.getBoolean("has_completed")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stages;
    }

    private void displayMapInHTMLView(JFrame frame, String mapHTML) {
        JEditorPane editorPane = new JEditorPane("text/html", mapHTML);
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        frame.getContentPane().removeAll();
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Helper classes to represent data
    class Junction {
        private final int junctionId;
        private final String name;

        public Junction(int junctionId, String name) {
            this.junctionId = junctionId;
            this.name = name;
        }

        public int getJunctionId() {
            return junctionId;
        }

        public String getName() {
            return name;
        }
    }

    class Stage {
        private final int stageId;
        private final String name;
        private final boolean completed;

        public Stage(int stageId, String name, boolean completed) {
            this.stageId = stageId;
            this.name = name;
            this.completed = completed;
        }

        public int getStageId() {
            return stageId;
        }

        public String getName() {
            return name;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}
