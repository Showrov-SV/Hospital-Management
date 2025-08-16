import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class Login {

    private Connection connection;

    public Login(Connection connection) {
        this.connection = connection;
        createLoginWindow();
    }

    private void createLoginWindow() {
        JFrame frame = new JFrame("Admin Login");
        frame.setSize(400, 360);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GradientPanel background = new GradientPanel();
        background.setLayout(new GridBagLayout());
        frame.add(background);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Admin Login", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        background.add(title, gbc);

        gbc.gridwidth = 1;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        JTextField userField = new JTextField(20);
        gbc.gridx=0; gbc.gridy=1; background.add(userLabel, gbc);
        gbc.gridx=1; gbc.gridy=1; background.add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passField = new JPasswordField(20);
        gbc.gridx=0; gbc.gridy=2; background.add(passLabel, gbc);
        gbc.gridx=1; gbc.gridy=2; background.add(passField, gbc);

        // Buttons vertical layout
        DarkNeonButton loginBtn = new DarkNeonButton("Login");
        DarkNeonButton signupBtn = new DarkNeonButton("Be an Admin");
        DarkNeonButton exitBtn = new DarkNeonButton("Exit");

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new GridLayout(3, 1, 0, 10)); // vertical buttons
        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);
        btnPanel.add(exitBtn);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        background.add(btnPanel, gbc);

        // Actions
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if(username.isEmpty() || password.isEmpty()) {
                showMessage(frame, "All fields are required!", false);
                return;
            }

            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM admins WHERE username=? AND password=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    Session.adminName = username;
                    showMessage(frame, "Login successful!", true);
                    frame.dispose();
                    new HospitalGUI(connection);
                } else {
                    showMessage(frame, "Invalid username or password!", false);
                }

            } catch(SQLException ex) {
                showMessage(frame, "Error: " + ex.getMessage(), false);
            }
        });

        signupBtn.addActionListener(e -> {
            frame.dispose();
            new Signup(connection);
        });

        exitBtn.addActionListener(e -> {
            showMessage(frame, "Thanks for using our service! [-,-]", true); // show message
            System.exit(0); // exit after dialog
        });


        frame.setVisible(true);
    }

    // Custom message dialog
    private void showMessage(JFrame parent, String msg, boolean success) {
        JDialog dialog = new JDialog(parent, "Message", true);
        dialog.setSize(480, 180); // bigger width and height
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // more padding
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(msg, JLabel.CENTER);
        label.setForeground(success ? new Color(0, 255, 150) : Color.RED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16)); // slightly larger font
        panel.add(label, BorderLayout.CENTER);

        DarkNeonButton okBtn = new DarkNeonButton("OK");
        okBtn.setPreferredSize(new Dimension(90, 40)); // slightly bigger button
        okBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(okBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }



    // Gradient background
    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = { new Color(0x02,0x00,0x24), new Color(0x09,0x09,0x79), new Color(0x00,0xD4,0xFF) };
        private int index = 0;
        private float progress = 0f;
        private final Timer timer;
        GradientPanel() { setDoubleBuffered(true); timer = new Timer(30,this); timer.start(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            Color c1 = blend(colors[index], colors[(index+1)%colors.length], progress);
            Color c2 = blend(colors[(index+1)%colors.length], colors[(index+2)%colors.length], progress);
            g2.setPaint(new GradientPaint(0,0,c1,w,h,c2));
            g2.fillRect(0,0,w,h);
        }
        @Override public void actionPerformed(ActionEvent e) { progress+=0.01f; if(progress>=1f){progress=0f; index=(index+1)%colors.length;} repaint(); }
        private Color blend(Color c1, Color c2, float ratio){ float r=c1.getRed()+(c2.getRed()-c1.getRed())*ratio; float g=c1.getGreen()+(c2.getGreen()-c1.getGreen())*ratio; float b=c1.getBlue()+(c2.getBlue()-c1.getBlue())*ratio; return new Color((int)r,(int)g,(int)b); }
    }

    // Dark neon button
    private static class DarkNeonButton extends JButton implements ActionListener {
        private float glowAlpha=0.3f; private boolean increasing=true; private final Timer timer;
        DarkNeonButton(String text){ super(text); setFocusPainted(false); setForeground(Color.WHITE); setFont(new Font("Segoe UI",Font.BOLD,16)); setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setContentAreaFilled(false); setOpaque(false); timer=new Timer(40,this); timer.start();}
        @Override public void actionPerformed(ActionEvent e){ if(increasing){ glowAlpha+=0.02f; if(glowAlpha>=0.7f) increasing=false;} else {glowAlpha-=0.02f; if(glowAlpha<=0.3f) increasing=true;} repaint();}
        @Override protected void paintComponent(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); int w=getWidth(),h=getHeight(); g2.setPaint(new GradientPaint(0,0,new Color(40,40,40),0,h,new Color(10,10,10))); g2.fill(new RoundRectangle2D.Double(0,0,w-1,h-1,20,20)); g2.setColor(new Color(0,200,255,(int)(glowAlpha*255))); g2.setStroke(new BasicStroke(3)); g2.draw(new RoundRectangle2D.Double(0,0,w-1,h-1,20,20)); FontMetrics fm=g2.getFontMetrics(); int tx=(w-fm.stringWidth(getText()))/2; int ty=(h+fm.getAscent()-fm.getDescent())/2-1; g2.setColor(Color.WHITE); g2.drawString(getText(),tx,ty); g2.dispose();}
    }
}
