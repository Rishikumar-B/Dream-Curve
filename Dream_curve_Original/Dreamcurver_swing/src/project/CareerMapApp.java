package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CareerMapApp {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/DreamCurve";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CareerMapApp::new);
    }

    public CareerMapApp() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Career Map Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        frame.add(mainPanel);

        // Button to create a new career map
        JButton createMapButton = new JButton("Create New Career Map");
        mainPanel.add(createMapButton);

        // Add action listener for the "Create Map" button
        createMapButton.addActionListener(e -> createNewCareerMap(frame));

        frame.setVisible(true);
    }

    private void createNewCareerMap(JFrame frame) {
        String mapName = JOptionPane.showInputDialog(frame, "Enter Career Map Name:");
        String userName = JOptionPane.showInputDialog(frame, "Enter User Name:");
        if (mapName != null && !mapName.isEmpty() && userName != null && !userName.isEmpty()) {
            CareerMap careerMap = new CareerMap(mapName, userName);
            insertCareerMap(careerMap);
            openCareerMapEditor(frame, careerMap);
        }
    }

    private void openCareerMapEditor(JFrame frame, CareerMap careerMap) {
        JFrame mapEditorFrame = new JFrame("Career Map Editor - " + careerMap.getName());
        mapEditorFrame.setSize(600, 400);
        mapEditorFrame.setLayout(new BorderLayout());

        JPanel junctionPanel = new JPanel();
        junctionPanel.setLayout(new BoxLayout(junctionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(junctionPanel);
        mapEditorFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton addJunctionButton = new JButton("Add Junction");
        controlPanel.add(addJunctionButton);
        mapEditorFrame.add(controlPanel, BorderLayout.SOUTH);

        addJunctionButton.addActionListener(e -> addJunction(junctionPanel, careerMap));

        mapEditorFrame.setVisible(true);
    }

    private void addJunction(JPanel junctionPanel, CareerMap careerMap) {
        String junctionName = JOptionPane.showInputDialog("Enter Junction Name:");
        if (junctionName != null && !junctionName.isEmpty()) {
            Junction junction = new Junction(junctionName, careerMap.getMapId());
            insertJunction(junction);

            JPanel junctionContainer = new JPanel();
            junctionContainer.setLayout(new BorderLayout());
            junctionContainer.setBorder(BorderFactory.createTitledBorder(junction.getName()));

            JPanel stagePanel = new JPanel();
            stagePanel.setLayout(new BoxLayout(stagePanel, BoxLayout.Y_AXIS));
            junctionContainer.add(stagePanel, BorderLayout.CENTER);

            JButton addStageButton = new JButton("Add Stage");
            addStageButton.addActionListener(e -> addStage(stagePanel, junction));
            junctionContainer.add(addStageButton, BorderLayout.SOUTH);

            junctionPanel.add(junctionContainer);
            junctionPanel.revalidate();
            junctionPanel.repaint();
        }
    }

    private void addStage(JPanel stagePanel, Junction junction) {
        String stageName = JOptionPane.showInputDialog("Enter Stage Name:");
        if (stageName != null && !stageName.isEmpty()) {
            Stage stage = new Stage(stageName, junction.getJunctionId());
            insertStage(stage);

            JPanel stageContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
            stageContainer.add(new JLabel(stage.getName()));

            JButton completeButton = new JButton("Mark as Complete");
            completeButton.addActionListener(e -> {
                stage.setCompleted(!stage.isCompleted());
                completeButton.setText(stage.isCompleted() ? "Completed" : "Mark as Complete");
                updateStageCompletion(stage);
            });
            stageContainer.add(completeButton);

            stagePanel.add(stageContainer);
            stagePanel.revalidate();
            stagePanel.repaint();
        }
    }

    // Database Insert Operations
    private void insertCareerMap(CareerMap careerMap) {
        String query = "INSERT INTO CareerMap_table (user_name, map_name, start_date, end_date, notes) VALUES (?, ?, NOW(), NOW(), ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, careerMap.getUserName());
            stmt.setString(2, careerMap.getName());
            stmt.setString(3, "Notes for " + careerMap.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                careerMap.setMapId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void insertJunction(Junction junction) {
        String query = "INSERT INTO junction_table (map_id, junction_name, create_date, end_date, notes) VALUES (?, ?, NOW(), NOW(), ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, junction.getMapId());
            stmt.setString(2, junction.getName());
            stmt.setString(3, "Notes for " + junction.getName());

            stmt.executeUpdate();

            // Get the generated junction_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                junction.setJunctionId(rs.getInt(1));  // Set the generated junction_id
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void insertStage(Stage stage) {
        // Query to insert a new stage into the Stages_table
        String query = "INSERT INTO Stages_table (junction_id, map_id, start_date, end_date, notes, has_completed) " +
                "VALUES (?, ?, NOW(), NOW(), ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Set the parameters for the prepared statement
            stmt.setInt(1, stage.getJunctionId());  // Set the junction_id
            stmt.setInt(2, getMapIdFromJunction(stage.getJunctionId()));  // Get the map_id based on the junction_id
            stmt.setString(3, "Notes for " + stage.getName());  // Set the notes
            stmt.setBoolean(4, stage.isCompleted());  // Set the completion status

            // Execute the update
            stmt.executeUpdate();

            // Get the generated keys (stage_id) if the insert was successful
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                stage.setStageId(rs.getInt(1));  // Set the generated stage_id
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Helper method to get the map_id for a given junction_id
    private int getMapIdFromJunction(int junctionId) {
        String query = "SELECT map_id FROM junction_table WHERE junction_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the junction_id parameter
            stmt.setInt(1, junctionId);

            // Execute the query and get the map_id
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("map_id");  // Return the map_id from the junction
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;  // Return -1 if no map_id was found (shouldn't happen if data is correct)
    }


    private void updateStageCompletion(Stage stage) {
        String query = "UPDATE Stages_table SET has_completed = ? WHERE stage_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, stage.isCompleted());
            stmt.setInt(2, stage.getStageId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

// CareerMap, Junction, and Stage classes for data modeling

class CareerMap {
    private int mapId;
    private final String name;
    private final String userName;

    public CareerMap(String name, String userName) {
        this.name = name;
        this.userName = userName;
    }

    public int getMapId() { return mapId; }
    public void setMapId(int mapId) { this.mapId = mapId; }
    public String getName() { return name; }
    public String getUserName() { return userName; }
}

class Junction {
    private int junctionId;
    private final String name;
    private final int mapId;

    public Junction(String name, int mapId) {
        this.name = name;
        this.mapId = mapId;
    }

    public int getJunctionId() { return junctionId; }
    public void setJunctionId(int junctionId) { this.junctionId = junctionId; }
    public String getName() { return name; }
    public int getMapId() { return mapId; }
}

class Stage {
    private int stageId;
    private final String name;
    private final int junctionId;
    private boolean isCompleted;

    public Stage(String name, int junctionId) {
        this.name = name;
        this.junctionId = junctionId;
        this.isCompleted = false;
    }

    public int getStageId() { return stageId; }
    public void setStageId(int stageId) { this.stageId = stageId; }
    public String getName() { return name; }
    public int getJunctionId() { return junctionId; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}

//. u crete the map on html with carerramap_table, junction_table, and stages_map,,,,,,,,,,,,,,,create the real map........... junction is conatains correspoding stages,,,,,this is to road map... ceatre the html css, wharn click the see map viesw button,,,,,,,, wirte html code in java swing