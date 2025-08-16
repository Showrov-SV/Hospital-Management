import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DoctorManagement {

    private Connection connection;
    private JFrame frame;
    private DefaultTableModel model;
    private JTable table;
    private String adminName; // current admin

    public DoctorManagement(Connection connection, String adminName) {
        this.connection = connection;
        this.adminName = adminName;
        createDoctorManagementWindow();
    }

    private void createDoctorManagementWindow() {
        frame = new JFrame("Doctor Management");
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        frame.add(background);

        // Title
        JLabel title = new JLabel("Doctor Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.add(title);
        background.add(topPanel, BorderLayout.NORTH);

        // Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        model = new DefaultTableModel(new String[]{"ID", "Name", "Specialization"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int column) { return column == 0 ? Integer.class : String.class; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 0, 0, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setOpaque(false);
        table.setFillsViewportHeight(true);

        // Disable column moving and resizing
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        // Column alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Name
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Specialization

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        background.add(tablePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Doctor");
        JButton updateBtn = new JButton("Update Doctor");
        JButton deleteBtn = new JButton("Delete Doctor");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn);
        styleButton(updateBtn);
        styleButton(deleteBtn);
        styleButton(refreshBtn);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addDoctorDialog());
        updateBtn.addActionListener(e -> updateDoctorDialog());
        deleteBtn.addActionListener(e -> deleteDoctorDialog());
        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
        frame.setVisible(true);
    }

    private void styleButton(JButton button) {
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

    private void refreshTable() {
        model.setRowCount(0);
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM doctors");
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization")
                });
            }

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void showGradientMessage(String message, String title) {
        JDialog dialog = new JDialog(frame, title, true);
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setForeground(Color.WHITE);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(msg, BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        styleButton(okBtn);
        JPanel btnPanel = new JPanel(); btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> dialog.dispose());

        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showDoctorDialog(String titleText, String name, String spec, boolean isUpdate, int id) {
        JDialog dialog = new JDialog(frame, titleText, true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(frame);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());
        dialog.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField nameField = new JTextField(name, 20);
        nameField.setHorizontalAlignment(JTextField.CENTER);

        JLabel specLabel = new JLabel("Specialization:");
        specLabel.setForeground(Color.WHITE);
        specLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField specField = new JTextField(spec, 20);
        specField.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(specLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(specField, gbc);

        JButton submitBtn = new JButton(isUpdate ? "Update" : "Add");
        styleButton(submitBtn);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                String doctorName = nameField.getText().trim();
                String specialization = specField.getText().trim();

                if (doctorName.isEmpty() || specialization.isEmpty()) {
                    showGradientMessage("All fields are required!", "Warning");
                    return;
                }

                if (isUpdate) {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE doctors SET name = ?, specialization = ?, updated_by = ? WHERE id = ?");
                    ps.setString(1, doctorName);
                    ps.setString(2, specialization);
                    ps.setString(3, adminName);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                    showGradientMessage("Doctor updated successfully!", "Success");
                } else {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO doctors (name, specialization, updated_by) VALUES (?, ?, ?)");
                    ps.setString(1, doctorName);
                    ps.setString(2, specialization);
                    ps.setString(3, adminName);
                    ps.executeUpdate();
                    showGradientMessage("Doctor added successfully!", "Success");
                }

                refreshTable();
                dialog.dispose();
            } catch (Exception ex) {
                showGradientMessage("Error: " + ex.getMessage(), "Error");
            }
        });

        dialog.setVisible(true);
    }

    private void addDoctorDialog() {
        showDoctorDialog("Add Doctor", "", "", false, -1);
    }

    private void updateDoctorDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showGradientMessage("Select a doctor to update", "Warning");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String spec = (String) model.getValueAt(row, 2);
        showDoctorDialog("Update Doctor", name, spec, true, id);
    }

    private void deleteDoctorDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showGradientMessage("Select a doctor to delete", "Warning");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        JDialog dialog = new JDialog(frame, true);
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel msg = new JLabel("Are you sure you want to delete this doctor?", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msg.setForeground(Color.WHITE);
        panel.add(msg, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(); btnPanel.setOpaque(false);
        JButton yesBtn = new JButton("YES");
        JButton noBtn = new JButton("NO");
        styleButton(yesBtn); styleButton(noBtn);
        btnPanel.add(yesBtn); btnPanel.add(noBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        yesBtn.addActionListener(e -> {
            try {
                PreparedStatement ps1 = connection.prepareStatement("DELETE FROM appointment WHERE doctor_id=?");
                ps1.setInt(1, id);
                ps1.executeUpdate();

                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM doctors WHERE id=?");
                ps2.setInt(1, id);
                int rows = ps2.executeUpdate();

                if (rows > 0) showGradientMessage("Doctor deleted successfully!", "Success");
                else showGradientMessage("No doctor found to delete", "Info");

                refreshTable();
            } catch (Exception ex) {
                showGradientMessage("Error: " + ex.getMessage(), "Error");
            }
            dialog.dispose();
        });

        noBtn.addActionListener(e -> dialog.dispose());

        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = { new Color(0x02,0x00,0x24), new Color(0x09,0x09,0x79), new Color(0x00,0xD4,0xFF) };
        private int index = 0; private float progress = 0f; private final Timer timer;
        GradientPanel(){ setDoubleBuffered(true); timer = new Timer(30,this); timer.start(); }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w=getWidth(), h=getHeight();
            Color c1=blend(colors[index],colors[(index+1)%colors.length],progress);
            Color c2=blend(colors[(index+1)%colors.length],colors[(index+2)%colors.length],progress);
            g2.setPaint(new GradientPaint(0,0,c1,w,h,c2)); g2.fillRect(0,0,w,h);
        }
        @Override public void actionPerformed(ActionEvent e){ progress+=0.01f; if(progress>=1f){progress=0f; index=(index+1)%colors.length;} repaint();}
        private Color blend(Color c1, Color c2, float ratio){ return new Color((int)(c1.getRed()+(c2.getRed()-c1.getRed())*ratio),(int)(c1.getGreen()+(c2.getGreen()-c1.getGreen())*ratio),(int)(c1.getBlue()+(c2.getBlue()-c1.getBlue())*ratio)); }
    }
}
