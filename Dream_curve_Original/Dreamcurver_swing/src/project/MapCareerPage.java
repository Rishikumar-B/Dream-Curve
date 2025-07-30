package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MapCareerPage extends JFrame {
    public MapCareerPage(String username) {
        setTitle("Career Map for " + username);
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);  // Set background to black

        // Main panel to hold everything, with black background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);

        // Orange panel for weightage information
        JPanel weightagePanel = new JPanel();
        weightagePanel.setBackground(Color.ORANGE);
        weightagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int baseWeightage = 40;
        int skillWeightage = 0;
        int interestWeightage = 0;
        int learningStyleWeightage = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DreamCurve", "postgres", "rishi@123")) {
            String query = "SELECT skills, interest, learningskills FROM UserProfile WHERE username = ?";
            PreparedStatement profileStmt = conn.prepareStatement(query);
            profileStmt.setString(1, username);

            ResultSet rs = profileStmt.executeQuery();
            if (rs.next()) {
                String skill = rs.getString("skills");
                String interest = rs.getString("interest");
                String learningStyle = rs.getString("learningskills");

                skillWeightage = getPointsFromTable(conn, "Skill", "sname", skill);
                interestWeightage = getPointsFromTable(conn, "Interest", "iname", interest);
                learningStyleWeightage = getPointsFromTable(conn, "LearningStyle", "lname", learningStyle);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        int totalWeightage = baseWeightage + skillWeightage + interestWeightage + learningStyleWeightage;

        JLabel weightageLabel = new JLabel("<html>Base Weightage: " + baseWeightage + "%<br>" +
                "Skill Weightage: " + skillWeightage + "%<br>" +
                "Interest Weightage: " + interestWeightage + "%<br>" +
                "Learning Style Weightage: " + learningStyleWeightage + "%<br><br>" +
                "<strong>Total Weightage: " + totalWeightage + "%</strong></html>");
        weightageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        weightageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        weightageLabel.setForeground(Color.BLACK);  // Set text color for contrast
        weightagePanel.add(weightageLabel);

        // Add weightage panel to the main panel
        mainPanel.add(weightagePanel);
        mainPanel.add(Box.createVerticalStrut(20));  // Spacer

        // Grey panel for career options
        JPanel careerPanel = new JPanel();
        careerPanel.setBackground(Color.GRAY);
        careerPanel.setLayout(new BoxLayout(careerPanel, BoxLayout.Y_AXIS));
        careerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Career Options"));

        // Fetch and display career options
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DreamCurve", "postgres", "rishi@123")) {
            String careerQuery = "SELECT career_option, points_calculation FROM career_reference WHERE points_calculation <= ?";
            PreparedStatement careerStmt = conn.prepareStatement(careerQuery);
            careerStmt.setInt(1, totalWeightage);

            ResultSet rs = careerStmt.executeQuery();
            while (rs.next()) {
                String careerOption = rs.getString("career_option");
                int careerPoints = rs.getInt("points_calculation");

                JPanel careerOptionPanel = new JPanel();
                careerOptionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                careerOptionPanel.setBackground(Color.GRAY);

                JLabel careerLabel = new JLabel(careerOption + " (" + careerPoints + "%)");
                careerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                careerLabel.setForeground(Color.WHITE);

                JButton generateMapButton = new JButton("Generate Career Map");
                generateMapButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Career Map for " + careerOption + " generated!");
                    }
                });

                careerOptionPanel.add(careerLabel);
                careerOptionPanel.add(generateMapButton);
                careerPanel.add(careerOptionPanel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Add career panel to the main panel
        mainPanel.add(careerPanel);

        // Add main panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    private int getPointsFromTable(Connection conn, String tableName, String columnName, String value) throws SQLException {
        String sql = "SELECT points FROM " + tableName + " WHERE " + columnName + " = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, value);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt("points") : 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapCareerPage("exampleUsername"));
    }
}
