package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class Profile extends JFrame {
    private JTextField usernameField, emailField, phoneNumberField, tenthMarksField, eleventhMarksField, twelfthMarksField, interestField, skillsField, learningSkillsField;
    private JButton submitButton, mapCareerButton, uploadButton;
    private File photoFile;
    private String lastInsertedUsername;

    public Profile() {
        setTitle("Profile Page");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(12, 2, 10, 10));
        profilePanel.setBackground(Color.CYAN);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        profilePanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        profilePanel.add(usernameField);

        profilePanel.add(new JLabel("Email ID:"));
        emailField = new JTextField();
        profilePanel.add(emailField);

        profilePanel.add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        profilePanel.add(phoneNumberField);

        profilePanel.add(new JLabel("Photo:"));
        uploadButton = new JButton("Upload Photo");
        uploadButton.addActionListener(e -> uploadPhoto());
        profilePanel.add(uploadButton);

        profilePanel.add(new JLabel("10th Marks:"));
        tenthMarksField = new JTextField();
        profilePanel.add(tenthMarksField);

        profilePanel.add(new JLabel("11th Marks:"));
        eleventhMarksField = new JTextField();
        profilePanel.add(eleventhMarksField);

        profilePanel.add(new JLabel("12th Marks:"));
        twelfthMarksField = new JTextField();
        profilePanel.add(twelfthMarksField);

        profilePanel.add(new JLabel("Interest:"));
        interestField = new JTextField();
        profilePanel.add(interestField);

        profilePanel.add(new JLabel("Skills:"));
        skillsField = new JTextField();
        profilePanel.add(skillsField);

        profilePanel.add(new JLabel("Learning Style:"));
        learningSkillsField = new JTextField();
        profilePanel.add(learningSkillsField);

        submitButton = new JButton("Submit");
        submitButton.setBackground(Color.GREEN);
        submitButton.addActionListener(e -> submitProfileData());

        mapCareerButton = new JButton("Map Career");
        mapCareerButton.setBackground(new Color(128, 0, 128));
        mapCareerButton.addActionListener(e -> {
            if (lastInsertedUsername != null) {
                new MapCareerPage(lastInsertedUsername);
            } else {
                JOptionPane.showMessageDialog(this, "Please submit your profile data first.");
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        mainPanel.add(profilePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(mapCareerButton);
        buttonPanel.setBackground(Color.BLACK);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mainPanel, gbc);

        gbc.gridy = 1;
        add(buttonPanel, gbc);
    }

    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            photoFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Photo uploaded: " + photoFile.getName());
        }
    }

    private void submitProfileData() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        int tenthMarks = Integer.parseInt(tenthMarksField.getText());
        int eleventhMarks = Integer.parseInt(eleventhMarksField.getText());
        int twelfthMarks = Integer.parseInt(twelfthMarksField.getText());
        String interest = interestField.getText();
        String skills = skillsField.getText();
        String learningSkills = learningSkillsField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DreamCurve", "postgres", "root")) {
            String sql = "INSERT INTO UserProfile (username, emailid, phonenumber, photo, tenthmarks, eleventhmarks, twelfthmarks, interest, skills, learningskills) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, phoneNumber);
            if (photoFile != null) {
                FileInputStream fis = new FileInputStream(photoFile);
                pstmt.setBinaryStream(4, fis, (int) photoFile.length());
            } else {
                pstmt.setNull(4, Types.BINARY);
            }
            pstmt.setInt(5, tenthMarks);
            pstmt.setInt(6, eleventhMarks);
            pstmt.setInt(7, twelfthMarks);
            pstmt.setString(8, interest);
            pstmt.setString(9, skills);
            pstmt.setString(10, learningSkills);

            pstmt.executeUpdate();
            lastInsertedUsername = username;
            JOptionPane.showMessageDialog(this, "Profile data saved successfully!");
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Profile Profile = new Profile();
            Profile.setVisible(true);
        });
    }
}
