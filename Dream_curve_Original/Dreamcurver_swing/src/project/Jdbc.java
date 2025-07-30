package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Jdbc {
    public static void main(String[] args) {
        String sql = "SELECT name FROM users WHERE id = 1";
        String url = "jdbc:postgresql://localhost:5432/DreamCurve"; // Default port is usually 5432
        String username = "postgres"; // Change this to the correct username
        String password = "root"; // Use the correct password

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {  // Check if there is a result
                String result = rs.getString(1);
                System.out.println("User Name: " + result);
            } else {
                System.out.println("No data found for the specified ID.");
            }

            // Close resources
            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
