import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Signup {

    private Connection connection;

    public Signup(Connection connection) {
        this.connection = connection;
        createSignupWindow();
    }

    private void createSignupWindow() {
        JFrame frame = new JFrame("Admin Sign Up");
        frame.setSize(400, 300);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton signupBtn = new JButton("Sign Up");
        JButton loginBtn = new JButton("Go to Login");

        gbc.gridx = 0; gbc.gridy = 0; frame.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; frame.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; frame.add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; frame.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; frame.add(signupBtn, gbc);
        gbc.gridy = 3; frame.add(loginBtn, gbc);

        signupBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO admins (username, password) VALUES (?, ?)");
                ps.setString(1, username);
                ps.setString(2, password); // For production, hash the password
                ps.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Signup successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new Login(connection); // Open login window
            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginBtn.addActionListener(e -> {
            frame.dispose();
            new Login(connection);
        });

        frame.setVisible(true);
    }
}
