import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login {

    private Connection connection;

    public Login(Connection connection) {
        this.connection = connection;
        createLoginWindow();
    }

    private void createLoginWindow() {
        JFrame frame = new JFrame("Admin Login");
        frame.setSize(400, 250);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginBtn = new JButton("Login");

        gbc.gridx=0; gbc.gridy=0; frame.add(userLabel, gbc);
        gbc.gridx=1; gbc.gridy=0; frame.add(userField, gbc);
        gbc.gridx=0; gbc.gridy=1; frame.add(passLabel, gbc);
        gbc.gridx=1; gbc.gridy=1; frame.add(passField, gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; frame.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM admins WHERE username=? AND password=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    Session.adminName = username; // store the logged-in admin name
                    JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();

// Open main dashboard
                    new HospitalGUI(connection);

                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}
