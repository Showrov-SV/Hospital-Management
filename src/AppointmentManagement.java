import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManagement {

    private Connection connection;
    private JFrame frame;
    private DefaultTableModel model;
    private JTable table;

    public AppointmentManagement(Connection connection) {
        this.connection = connection;
        createAppointmentWindow();
    }

    private void createAppointmentWindow() {
        frame = new JFrame("Appointment Management");
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        frame.add(background);

        // Title
        JLabel title = new JLabel("Appointment Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.add(title);
        background.add(topPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Updated table model with Admin column
        model = new DefaultTableModel(
                new String[]{"ID", "Patient Name", "Doctor Name", "Date", "Admin"}, 0
        ) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : String.class; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 0, 0, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setOpaque(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false); // Disable column reordering

        // Column alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Patient Name
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Doctor Name
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Date
        table.getColumnModel().getColumn(4).setCellRenderer(leftRenderer);   // Admin

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        background.add(tablePanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton bookBtn = new JButton("Book Appointment");
        JButton refreshBtn = new JButton("Refresh");
        styleButton(bookBtn);
        styleButton(refreshBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(refreshBtn);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        bookBtn.addActionListener(e -> bookAppointment());
        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
        frame.setVisible(true);
    }

    private void styleButton(JButton button){
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0,150,255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt){ button.setBackground(new Color(0,200,255)); }
            public void mouseExited(MouseEvent evt){ button.setBackground(new Color(0,150,255)); }
        });
    }

    private void refreshTable(){
        model.setRowCount(0);
        try {
            String query = "SELECT a.id, p.name AS patient_name, d.name AS doctor_name, " +
                    "a.appointment_date, a.updated_by " +
                    "FROM appointment a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id";

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("appointment_date"),
                        rs.getString("updated_by")  // <-- Admin column
                });
            }
        } catch(SQLException e){
            e.printStackTrace();
            showGradientMessage("Error loading appointments: " + e.getMessage(), "Error");
        }
    }

    // --- Gradient message dialog ---
    private void showGradientMessage(String message, String title){
        JDialog dialog = new JDialog(frame, title, true);
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(msg, BorderLayout.CENTER);

        JButton okBtn = new JButton("OK"); styleButton(okBtn);
        JPanel btnPanel = new JPanel(); btnPanel.setOpaque(false);
        btnPanel.add(okBtn); panel.add(btnPanel, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> dialog.dispose());

        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void bookAppointment() {
        try {
            String[] patients = getNames("patients");
            String[] doctors = getNames("doctors");

            if (patients.length == 0 || doctors.length == 0) {
                showGradientMessage("No patients or doctors available!", "Warning");
                return;
            }

            JDialog dialog = new JDialog(frame, "Book Appointment", true);
            GradientPanel panel = new GradientPanel();
            panel.setLayout(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            dialog.setContentPane(panel);
            dialog.setResizable(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel patientLabel = new JLabel("Select Patient:");
            JLabel doctorLabel = new JLabel("Select Doctor:");
            JLabel dateLabel = new JLabel("Appointment Date (YYYY-MM-DD):");
            patientLabel.setForeground(Color.WHITE);
            doctorLabel.setForeground(Color.WHITE);
            dateLabel.setForeground(Color.WHITE);
            patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            doctorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JComboBox<String> patientBox = new JComboBox<>(patients);
            JComboBox<String> doctorBox = new JComboBox<>(doctors);
            JTextField dateField = new JTextField(LocalDate.now().toString());

            gbc.gridx = 0; gbc.gridy = 0; panel.add(patientLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 0; panel.add(patientBox, gbc);
            gbc.gridx = 0; gbc.gridy = 1; panel.add(doctorLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 1; panel.add(doctorBox, gbc);
            gbc.gridx = 0; gbc.gridy = 2; panel.add(dateLabel, gbc);
            gbc.gridx = 1; gbc.gridy = 2; panel.add(dateField, gbc);

            JButton bookBtn = new JButton("Book");
            JButton cancelBtn = new JButton("Cancel");
            styleButton(bookBtn);
            styleButton(cancelBtn);

            JPanel btnPanel = new JPanel(); btnPanel.setOpaque(false);
            btnPanel.add(bookBtn); btnPanel.add(cancelBtn);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            panel.add(btnPanel, gbc);

            // Book action with Admin tracking
            bookBtn.addActionListener(e -> {
                try {
                    int patientId = getIdByName("patients", (String) patientBox.getSelectedItem());
                    int doctorId = getIdByName("doctors", (String) doctorBox.getSelectedItem());
                    String appointmentDate = dateField.getText();

                    if (!checkDoctorAvailability(doctorId, appointmentDate)) {
                        showGradientMessage("Doctor is not available on this date!", "Warning");
                        return;
                    }

                    String insertQuery = "INSERT INTO appointment (patient_id, doctor_id, appointment_date, updated_by) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = connection.prepareStatement(insertQuery);
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setString(3, appointmentDate);
                    ps.setString(4, Session.adminName); // store admin who booked
                    int rows = ps.executeUpdate();

                    showGradientMessage(rows > 0 ? "Appointment booked!" : "Failed to book", "Info");
                    refreshTable();
                    dialog.dispose();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showGradientMessage("Error: " + ex.getMessage(), "Error");
                }
            });

            cancelBtn.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            showGradientMessage("Error: " + e.getMessage(), "Error");
        }
    }

    private String[] getNames(String table) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT name FROM " + table);
        List<String> list = new ArrayList<>();
        while(rs.next()) list.add(rs.getString("name"));
        return list.toArray(new String[0]);
    }

    private int getIdByName(String table, String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT id FROM "+table+" WHERE name=?");
        ps.setString(1,name);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("id") : -1;
    }

    private boolean checkDoctorAvailability(int doctorId, String date){
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM appointment WHERE doctor_id=? AND appointment_date=?");
            ps.setInt(1, doctorId); ps.setString(2,date);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1)==0;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // --- Gradient panel ---
    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = { new Color(0x02,0x00,0x24), new Color(0x09,0x09,0x79), new Color(0x00,0xD4,0xFF) };
        private int index=0; private float progress=0f; private final Timer timer;
        GradientPanel(){ setDoubleBuffered(true); timer=new Timer(30,this); timer.start(); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w=getWidth(), h=getHeight();
            Color c1=blend(colors[index],colors[(index+1)%colors.length],progress);
            Color c2=blend(colors[(index+1)%colors.length],colors[(index+2)%colors.length],progress);
            g2.setPaint(new GradientPaint(0,0,c1,w,h,c2)); g2.fillRect(0,0,w,h);
        }
        @Override public void actionPerformed(ActionEvent e){ progress+=0.01f; if(progress>=1f){progress=0f; index=(index+1)%colors.length;} repaint(); }
        private Color blend(Color c1, Color c2, float ratio){
            return new Color((int)(c1.getRed()+(c2.getRed()-c1.getRed())*ratio),
                    (int)(c1.getGreen()+(c2.getGreen()-c1.getGreen())*ratio),
                    (int)(c1.getBlue()+(c2.getBlue()-c1.getBlue())*ratio));
        }
    }
}
