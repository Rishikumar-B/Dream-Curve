import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton studentRadioButton, mentorRadioButton;

    public LoginPage() {
        setTitle("Sign_in Page");

        // Set JFrame to maximize and cover the whole screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create background panel with scaled image
        JLabel background = new JLabel();
        background.setLayout(new GridBagLayout()); // Center-align components
        background.setIcon(getScaledBackgroundImage("C:\\Users\\bprts\\IdeaProjects\\Dreamcurver_swing\\src\\Images\\newloginbg.jpg"));
        add(background);

        // Transparent panel for login form
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // Transparent panel
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Career Path Navigator Login");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE); // Set text color for visibility

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        studentRadioButton = new JRadioButton("Student");
        mentorRadioButton = new JRadioButton("Mentor");
        studentRadioButton.setOpaque(false);
        mentorRadioButton.setOpaque(false);
        studentRadioButton.setForeground(Color.WHITE);
        mentorRadioButton.setForeground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(studentRadioButton);
        group.add(mentorRadioButton);

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new LoginButtonListener());

        // Add components to the panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(new JLabel("Username:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);
        mainPanel.add(studentRadioButton);
        mainPanel.add(mentorRadioButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loginButton);

        background.add(mainPanel); // Add login form panel to the background label

        setVisible(true);
    }

    // Method to scale background image to fit the screen
    private Icon getScaledBackgroundImage(String imagePath) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userType = studentRadioButton.isSelected() ? "Student" : "Mentor";

            try {
                // Hash the password before storing it in the database
                String hashedPassword = hashPassword(password);

                // Get the current timestamp
                Timestamp loginTime = Timestamp.from(Instant.now());

                // Insert into appropriate table
                insertLoginData(username, hashedPassword, userType, loginTime);

                JOptionPane.showMessageDialog(LoginPage.this, "Login Successful for " + userType + "!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginPage.this, "Login failed. Please try again.");
            }
        }
    }

    private void insertLoginData(String username, String hashedPassword, String userType, Timestamp loginTime) throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/DreamCurve";
        String dbUsername = "root";
        String dbPassword = "root";

        String tableName = userType.equals("Student") ? "Studentlogin" : "Mentorlogin";
        String insertQuery = "INSERT INTO " + tableName + " (username, password, timeoflogin) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setTimestamp(3, loginTime);
            stmt.executeUpdate();
        }
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
