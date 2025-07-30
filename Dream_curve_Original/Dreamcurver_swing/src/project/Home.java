package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends JFrame {

    private String username;

    // Panels for layout
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel centerPanel;
    private JComboBox<String> themeComboBox;
    private List<JPanel> achievementBoxes;
    private int currentBoxIndex = 0;

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    private Connection conn;
    private JLabel usernameLabel; // Label to display the current user's name

    private JPanel slideMenu; // The sliding menu panel
    private boolean isMenuVisible = false; // Menu visibility state

    public Home(String username) {
        this.username = username;  // Store the username
        setTitle("DreamCurve");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectDatabase();

        mainPanel = new JPanel(new BorderLayout());
        leftPanel = new JPanel();
        centerPanel = new JPanel(new BorderLayout());

        // Display Current User's Name at the top
        usernameLabel = new JLabel("Welcome, " + username, SwingConstants.LEFT);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);

        // Create a panel for the top section (title + username)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.add(usernameLabel, BorderLayout.WEST); // Add username label on the left
        topPanel.add(new JLabel("DreamCurve", SwingConstants.CENTER), BorderLayout.CENTER); // Title in the center

        mainPanel.add(topPanel, BorderLayout.NORTH); // Add the topPanel to the main panel

        String[] themes = {"Black", "White", "Red", "Blue", "Green", "Orange", "Yellow", "Violet"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.addActionListener(e -> updateTheme((String) themeComboBox.getSelectedItem()));

        // Add the theme combo box to the panel
        JPanel themePanel = new JPanel();
        themePanel.setBackground(Color.WHITE);
        themePanel.add(new JLabel("Select Theme:"));
        themePanel.add(themeComboBox);

        // Add theme selection to the center panel
        centerPanel.add(themePanel, BorderLayout.NORTH);

        JTextArea descriptionText = new JTextArea("DreamCurve is a career guidance application ...");
        descriptionText.setEditable(false);
        descriptionText.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        centerPanel.add(descriptionText, BorderLayout.CENTER);

        leftPanel.setLayout(new GridLayout(7, 1, 10, 10));

        // Add slide menu toggle button
        JButton slideMenuButton = new JButton("☰");
        slideMenuButton.setFont(new Font("Arial", Font.BOLD, 20));
        slideMenuButton.setForeground(Color.WHITE);
        slideMenuButton.setBackground(Color.DARK_GRAY);
        slideMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleMenu();
            }
        });
        mainPanel.add(slideMenuButton, BorderLayout.WEST);

        // Side Sliding Menu Panel
        slideMenu = createSlideMenu();
        mainPanel.add(slideMenu, BorderLayout.WEST); // Add the slide menu to the main panel

        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUsers(searchField.getText().trim());
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Users:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        centerPanel.add(searchPanel, BorderLayout.SOUTH);
        centerPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel achievementsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        achievementBoxes = createAchievementBoxes();
        updateAchievementBoxes(achievementsPanel);
        centerPanel.add(achievementsPanel, BorderLayout.SOUTH);

        updateTheme("White");
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
        startSlideshow(achievementsPanel);
        displayRandomUsers();
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/DreamCurve", "postgres", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) return;
        userListModel.clear();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT username FROM user_table WHERE username ILIKE ?");
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userListModel.addElement(rs.getString("username"));
            }

            // Add listener to open Profile when a username is clicked
            userList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null) {
                        viewProfile(selectedUser);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewProfile(String username) {
        // Create a new Profile frame for the selected user
        new Profile();
    }

    private void displayRandomUsers() {
        userListModel.clear();  // Clear the current list

        try {
            // SQL query to select all user details excluding the password
            String query = "SELECT username, phone, emailid, typeofuser FROM user_table ORDER BY random() LIMIT 5";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");  // Get the username
                String phone = rs.getString("phone");        // Get the phone number
                String email = rs.getString("emailid");     // Get the email
                String typeofuser = rs.getString("typeofuser");  // Get the type of user

                // Create a display string with all the details (excluding password)
                String userDetails = String.format("Username: %s\nPhone: %s\nEmail: %s\nType: %s",
                        username, phone, email, typeofuser);

                // Create a button that displays the user's details
                JButton menuButton = new JButton("<html><b>Progress Tracker for " + username + "</b><br>" + userDetails + "</html>");

                // Add an ActionListener to the button to open ProgressTrackerPage when clicked
                menuButton.addActionListener(e -> openProgressTrackerPage(username));

                // Optionally, add the username to the user list model for search purposes
                userListModel.addElement(username);

                // Add the button to the leftPanel or wherever you want to display it
                leftPanel.add(menuButton);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle SQL exceptions
        }
    }




    private void openProgressTrackerPage(String username) {
        // Create and display the ProgressTrackerPage for the given username
        new ProgressTrackerPage(username).setVisible(true);
    }


    private JPanel createSlideMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Color.DARK_GRAY);
        menu.setPreferredSize(new Dimension(250, getHeight()));

        // Add menu items with delayed instantiation
        addMenuButton(menu, "Progress Tracker", () -> new ProgressTrackerPage(username));
        addMenuButton(menu, "Profile", () -> new Profile());
      //  addMenuButton(menu, "Create Map", () -> new CareerMapApp());  // Pass username to CareerMapApp
        addMenuButton(menu, "Recommendation", () -> new Recommendation());
        addMenuButton(menu, "Mentor Details", () -> new MentorDetails());
        addMenuButton(menu, "Settings", () -> new Settings());
        addMenuButton(menu, "Update Details", () -> new UpdateDetails());

        return menu;
    }

    private void addMenuButton(JPanel menu, String label, Runnable frameCreator) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.GRAY);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Action to create and display the frame when the button is clicked
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the frame creator (it will create and show the frame only when clicked)
                frameCreator.run();
            }
        });

        menu.add(button);
    }

    private void toggleMenu() {
        int targetWidth = isMenuVisible ? 0 : 250; // If menu is visible, collapse it to 0 width, otherwise expand to 250.
        Timer timer = new Timer();
        final int[] currentWidth = {slideMenu.getWidth()}; // Get current width of the menu.

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Slide the menu in or out.
                if (currentWidth[0] != targetWidth) {
                    currentWidth[0] += (targetWidth > currentWidth[0]) ? 10 : -10; // Increment/decrement width by 10 pixels.
                    slideMenu.setPreferredSize(new Dimension(currentWidth[0], getHeight()));
                    slideMenu.revalidate();
                } else {
                    cancel(); // Stop the timer once the menu has reached the target width.
                }
            }
        }, 0, 10); // Update every 10 milliseconds for smooth transition.

        isMenuVisible = !isMenuVisible; // Toggle visibility.
    }

    private void updateTheme(String theme) {
        Color bgColor;
        switch (theme) {
            case "White" -> bgColor = Color.WHITE;
            case "Red" -> bgColor = Color.RED;
            case "Blue" -> bgColor = Color.BLUE;
            case "Green" -> bgColor = Color.GREEN;
            case "Orange" -> bgColor = Color.ORANGE;
            case "Yellow" -> bgColor = Color.YELLOW;
            case "Violet" -> bgColor = new Color(138, 43, 226);
            default -> bgColor = Color.BLACK;
        }
        mainPanel.setBackground(bgColor);
        leftPanel.setBackground(bgColor);
        centerPanel.setBackground(bgColor);
        slideMenu.setBackground(bgColor);
        repaint();
    }

    private void startSlideshow(JPanel achievementsPanel) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    achievementsPanel.removeAll();
                    updateAchievementBoxes(achievementsPanel);
                    achievementsPanel.revalidate();
                    achievementsPanel.repaint();
                });
            }
        }, 0, 10000);
    }

    private void updateAchievementBoxes(JPanel achievementsPanel) {
        achievementsPanel.add(achievementBoxes.get(currentBoxIndex));
        achievementsPanel.add(achievementBoxes.get((currentBoxIndex + 1) % achievementBoxes.size()));
        achievementsPanel.add(achievementBoxes.get((currentBoxIndex + 2) % achievementBoxes.size()));
        currentBoxIndex = (currentBoxIndex + 1) % achievementBoxes.size();
    }

    private List<JPanel> createAchievementBoxes() {
        List<JPanel> boxes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            JPanel box = new JPanel();
            box.setPreferredSize(new Dimension(250, 100));
            box.setBackground(new Color(0, 0, 0, 50)); // Semi-transparent black
            box.add(new JLabel("Achievement " + (i + 1)));
            boxes.add(box);
        }
        return boxes;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Home("user123"));
    }

    // Placeholder classes for the target frames (Profile, Analyse, etc.)



    static class Recommendation extends JFrame {
        public Recommendation() {
            setTitle("Recommendation");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(new JLabel("Welcome to Recommendation", SwingConstants.CENTER));
            setVisible(true);
        }
    }

    static class MentorDetails extends JFrame {
        public MentorDetails() {
            setTitle("Mentor Details");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(new JLabel("Welcome to Mentor Details", SwingConstants.CENTER));
            setVisible(true);
        }
    }

    static class Settings extends JFrame {
        public Settings() {
            setTitle("Settings");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(new JLabel("Welcome to Settings", SwingConstants.CENTER));
            setVisible(true);
        }
    }

    static class UpdateDetails extends JFrame {
        public UpdateDetails() {
            setTitle("Update Details");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            add(new JLabel("Welcome to Update Details", SwingConstants.CENTER));
            setVisible(true);
        }
    }
}