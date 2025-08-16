import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PatientManagement {

    private Connection connection;
    private JFrame frame;
    private DefaultTableModel model;
    private JTable table;

    public PatientManagement(Connection connection) {
        this.connection = connection;
        createPatientManagementWindow();
    }

    private void createPatientManagementWindow() {
        frame = new JFrame("Patient Management");
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        frame.add(background);

        // Title
        JLabel title = new JLabel("Patient Management");
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

        model = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender"}, 0) {
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 2) return Integer.class;
                return String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(0, 0, 0, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setOpaque(false);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        background.add(tablePanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Patient");
        JButton updateBtn = new JButton("Update Patient");
        JButton deleteBtn = new JButton("Delete Patient");
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

        // Button actions
        addBtn.addActionListener(e -> addPatientDialog());
        updateBtn.addActionListener(e -> updatePatientDialog());
        deleteBtn.addActionListener(e -> deletePatientDialog());
        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable();
        frame.setVisible(true);
        // inside createPatientManagementWindow() after creating table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 150, 255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(new Color(0, 200, 255)); }
            public void mouseExited(MouseEvent evt) { button.setBackground(new Color(0, 150, 255)); }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM patients");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading patients: " + e.getMessage());
        }
    }
    private void showPatientDialog(String titleText, String name, String age, String gender, boolean isUpdate, int id) {
        JDialog dialog = new JDialog(frame, titleText, true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(frame);

        GradientPanel panel = new GradientPanel();
        panel.setLayout(new GridBagLayout());
        dialog.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField nameField = new JTextField(name, 20);

        // Age
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField ageField = new JTextField(age, 20);

        // Gender (combo box)
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String[] genders = {"Male", "Female"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        if (!gender.isEmpty()) {
            genderCombo.setSelectedItem(gender);
        }

        // Add components to panel
        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(ageLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(ageField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(genderLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(genderCombo, gbc);

        // Submit button
        JButton submitBtn = new JButton(isUpdate ? "Update" : "Add");
        styleButton(submitBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty() || genderCombo.getSelectedItem() == null) {
                    showGradientMessage("All fields are required!", "Warning");
                    return;
                }

                String selectedGender = (String) genderCombo.getSelectedItem();
                String adminName = Session.adminName; // current admin

                if (isUpdate) {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE patients SET name = ?, age = ?, gender = ?, updated_by = ? WHERE id = ?");
                    ps.setString(1, nameField.getText());
                    ps.setInt(2, Integer.parseInt(ageField.getText()));
                    ps.setString(3, selectedGender);
                    ps.setString(4, adminName); // store admin
                    ps.setInt(5, id);
                    ps.executeUpdate();
                    showGradientMessage("Patient updated successfully!", "Success");
                } else {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO patients (name, age, gender, updated_by) VALUES (?, ?, ?, ?)");
                    ps.setString(1, nameField.getText());
                    ps.setInt(2, Integer.parseInt(ageField.getText()));
                    ps.setString(3, selectedGender);
                    ps.setString(4, adminName); // store admin
                    ps.executeUpdate();
                    showGradientMessage("Patient added successfully!", "Success");
                }
                refreshTable();
                dialog.dispose();
            } catch(NumberFormatException ex) {
                showGradientMessage("Age must be a number!", "Error");
            } catch(Exception ex) {
                showGradientMessage("Error: " + ex.getMessage(), "Error");
            }
        });

        dialog.setVisible(true);
    }





    private void addPatientDialog() {
        showPatientDialog("Add Patient", "", "", "", false, -1);
    }

    private void updatePatientDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            // Gradient warning dialog
            GradientPanel warningPanel = new GradientPanel();
            warningPanel.setLayout(new BorderLayout(10, 10));
            warningPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            JLabel msg = new JLabel("Select a patient to update");
            msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
            msg.setForeground(Color.WHITE);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            warningPanel.add(msg, BorderLayout.CENTER);

            JButton okBtn = new JButton("OK");
            styleButton(okBtn);
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.add(okBtn);
            warningPanel.add(btnPanel, BorderLayout.SOUTH);

            JDialog dialog = new JDialog(frame, true);
            dialog.setUndecorated(true);
            dialog.setContentPane(warningPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);

            okBtn.addActionListener(e -> dialog.dispose());
            dialog.setVisible(true);

            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String age = model.getValueAt(row, 2).toString();
        String gender = (String) model.getValueAt(row, 3);
        showPatientDialog("Update Patient", name, age, gender, true, id);
    }


    private void deletePatientDialog() {
        int row = table.getSelectedRow();
        if (row == -1) {
            // Gradient warning dialog
            GradientPanel warningPanel = new GradientPanel();
            warningPanel.setLayout(new BorderLayout(10, 10));
            warningPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            JLabel msg = new JLabel("Select a patient to delete");
            msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
            msg.setForeground(Color.WHITE);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            warningPanel.add(msg, BorderLayout.CENTER);

            JButton okBtn = new JButton("OK");
            styleButton(okBtn);
            JPanel btnPanel = new JPanel();
            btnPanel.setOpaque(false);
            btnPanel.add(okBtn);
            warningPanel.add(btnPanel, BorderLayout.SOUTH);

            JDialog dialog = new JDialog(frame, true);
            dialog.setUndecorated(true);
            dialog.setContentPane(warningPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(frame);

            okBtn.addActionListener(e -> dialog.dispose());
            dialog.setVisible(true);

            return;
        }


        int id = (int) model.getValueAt(row, 0);

        // Gradient panel for dialog
        GradientPanel dialogPanel = new GradientPanel();
        dialogPanel.setLayout(new BorderLayout(20, 20));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel msg = new JLabel("Are you sure you want to delete this patient?");
        msg.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msg.setForeground(Color.WHITE);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        dialogPanel.add(msg, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton yesBtn = new JButton("YES");
        JButton noBtn = new JButton("NO");
        styleButton(yesBtn);
        styleButton(noBtn);
        btnPanel.add(yesBtn);
        btnPanel.add(noBtn);
        dialogPanel.add(btnPanel, BorderLayout.SOUTH);

        // Custom JDialog
        JDialog dialog = new JDialog(frame, true);
        dialog.setUndecorated(true);
        dialog.setContentPane(dialogPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        yesBtn.addActionListener(e -> {
            try {
                PreparedStatement ps1 = connection.prepareStatement("DELETE FROM appointment WHERE patient_id = ?");
                ps1.setInt(1, id);
                ps1.executeUpdate();

                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM patients WHERE id = ?");
                ps2.setInt(1, id);
                int rows = ps2.executeUpdate();

                // Use gradient message instead of JOptionPane
                if(rows > 0) {
                    showGradientMessage("Patient deleted successfully!", "Success");
                } else {
                    showGradientMessage("No patient found to delete.", "Info");
                }

                refreshTable();
            } catch (SQLException ex) {
                showGradientMessage("Error deleting patient: " + ex.getMessage(), "Error");
            }
            dialog.dispose();
        });


        noBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }



    // Gradient panel
    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = { new Color(0x02,0x00,0x24), new Color(0x09,0x09,0x79), new Color(0x00,0xD4,0xFF) };
        private int index = 0; private float progress = 0f; private final Timer timer;
        GradientPanel() { setDoubleBuffered(true); timer = new Timer(30,this); timer.start(); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            Color c1 = blend(colors[index], colors[(index+1)%colors.length], progress);
            Color c2 = blend(colors[(index+1)%colors.length], colors[(index+2)%colors.length], progress);
            g2.setPaint(new GradientPaint(0,0,c1,w,h,c2));
            g2.fillRect(0,0,w,h);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            progress+=0.01f; if(progress>=1f){progress=0f; index=(index+1)%colors.length;} repaint();
        }
        private Color blend(Color c1, Color c2, float ratio){
            float r=c1.getRed()+(c2.getRed()-c1.getRed())*ratio;
            float g=c1.getGreen()+(c2.getGreen()-c1.getGreen())*ratio;
            float b=c1.getBlue()+(c2.getBlue()-c1.getBlue())*ratio;
            return new Color((int)r,(int)g,(int)b);
        }
    }
    private void showGradientMessage(String message, String title) {
        GradientPanel panel = new GradientPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(msgLabel, BorderLayout.CENTER);

        JButton okBtn = new JButton("OK");
        styleButton(okBtn);
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(frame, title, true);
        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        okBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

}
