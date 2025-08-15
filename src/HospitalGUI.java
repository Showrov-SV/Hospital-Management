import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HospitalGUI {


    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "pass123";

    private Connection connection;

    public HospitalGUI() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            e.printStackTrace();
            System.exit(1);
        }
        createMainMenu();
    }

    private void createMainMenu() {
        JFrame frame = new JFrame("Hospital Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1, 10, 10));

        JButton addPatientBtn = new JButton("Add Patient");
        JButton viewPatientsBtn = new JButton("View Patients");
        JButton viewDoctorsBtn = new JButton("View Doctors");
        JButton bookAppointmentBtn = new JButton("Book Appointment");
        JButton exitBtn = new JButton("Exit");

        addPatientBtn.addActionListener(e -> addPatientWindow());
        viewPatientsBtn.addActionListener(e -> viewPatientsWindow());
        viewDoctorsBtn.addActionListener(e -> viewDoctorsWindow());
        bookAppointmentBtn.addActionListener(e -> bookAppointmentWindow());
        exitBtn.addActionListener(e -> System.exit(0));

        frame.add(addPatientBtn);
        frame.add(viewPatientsBtn);
        frame.add(viewDoctorsBtn);
        frame.add(bookAppointmentBtn);
        frame.add(exitBtn);

        frame.setVisible(true);
    }

    private void addPatientWindow() {
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField genderField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Age:", ageField,
                "Gender:", genderField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Add Patient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String query = "INSERT INTO patients (name, age, gender) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, nameField.getText());
                ps.setInt(2, Integer.parseInt(ageField.getText()));
                ps.setString(3, genderField.getText());
                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(null, rows > 0 ? "Patient added!" : "Failed to add patient");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error adding patient");
            }
        }
    }

    private void viewPatientsWindow() {
        showTable("SELECT * FROM patients", new String[]{"ID", "Name", "Age", "Gender"});
    }

    private void viewDoctorsWindow() {
        showTable("SELECT * FROM doctors", new String[]{"ID", "Name", "Specialization"});
    }

    private void bookAppointmentWindow() {
        JComboBox<String> patientBox = new JComboBox<>();
        JComboBox<String> doctorBox = new JComboBox<>();
        JTextField dateField = new JTextField("YYYY-MM-DD");

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, name FROM patients");
            while (rs.next()) {
                patientBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
            rs = st.executeQuery("SELECT id, name FROM doctors");
            while (rs.next()) {
                doctorBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[] fields = {
                "Select Patient:", patientBox,
                "Select Doctor:", doctorBox,
                "Appointment Date:", dateField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Book Appointment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int patientId = Integer.parseInt(patientBox.getSelectedItem().toString().split(" - ")[0]);
                int doctorId = Integer.parseInt(doctorBox.getSelectedItem().toString().split(" - ")[0]);
                String date = dateField.getText();

                if (isDoctorAvailable(doctorId, date)) {
                    String query = "INSERT INTO appointment (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setString(3, date);
                    int rows = ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, rows > 0 ? "Appointment booked!" : "Failed to book");
                } else {
                    JOptionPane.showMessageDialog(null, "Doctor not available on that date!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error booking appointment");
            }
        }
    }

    private boolean isDoctorAvailable(int doctorId, String date) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, doctorId);
        ps.setString(2, date);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
        return false;
    }

    private void showTable(String query, String[] columns) {
        JFrame frame = new JFrame();
        frame.setSize(500, 400);

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Object[] rowData = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalGUI::new);
    }
}
