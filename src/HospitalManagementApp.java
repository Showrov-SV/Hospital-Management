import java.sql.*;

public class HospitalManagementApp {

    public static void main(String[] args) {
        try {
            // Connect to MySQL database
            String url = "jdbc:mysql://localhost:3306/hospital"; // replace with your DB name
            String user = "root"; // replace with your DB user
            String password = "pass123"; // replace with your DB password

            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully!");

            // Open Signup/Login first
            new Signup(connection); // Admin can choose to signup or go to login

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
