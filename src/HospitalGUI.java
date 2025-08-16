import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class HospitalGUI extends JFrame {

    private Connection connection;

    public HospitalGUI(Connection connection) {
        this.connection = connection;

        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Left panel for buttons
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setOpaque(false);
        DarkNeonButton contactBtn = new DarkNeonButton("Contact");
        DarkNeonButton logoutBtn = new DarkNeonButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
                    new Login(connection);
        });

        leftButtons.add(contactBtn);
        leftButtons.add(logoutBtn);
        topBar.add(leftButtons, BorderLayout.WEST);

        // Right panel for hospital title
        JLabel title = new JLabel("Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        JPanel rightTitle = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTitle.setOpaque(false);
        rightTitle.add(title);
        topBar.add(rightTitle, BorderLayout.EAST);

        // Background panel with animated gradient
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        add(background, BorderLayout.CENTER);
        background.add(topBar, BorderLayout.NORTH);

        // Hero section with Heart Rhythm 2D animation
        HeroPanel hero = new HeroPanel();
        background.add(hero, BorderLayout.CENTER);

        // Cards row
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 24, 24));
        cardsRow.setOpaque(false);
        cardsRow.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        cardsRow.add(createCard("Manage Patients", e -> new PatientManagement(connection)));
        cardsRow.add(createCard("View Doctors", e -> new DoctorManagement(connection, Session.adminName)));
        cardsRow.add(createCard("Book Appointment", e -> new AppointmentManagement(connection)));
        background.add(cardsRow, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createCard(String title, ActionListener onClick) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fill(new RoundRectangle2D.Double(6, 8, getWidth() - 12, getHeight() - 12, 26, 26));
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 6, getHeight() - 6, 26, 26));
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        DarkNeonButton btn = new DarkNeonButton("Open");
        btn.setPreferredSize(new Dimension(140, 42));
        btn.addActionListener(onClick);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(btn);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    // Gradient background
    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = { new Color(0x02, 0x00, 0x24), new Color(0x09, 0x09, 0x79), new Color(0x00, 0xD4, 0xFF) };
        private int index = 0;
        private float progress = 0f;
        private final Timer timer;

        GradientPanel() {
            setDoubleBuffered(true);
            timer = new Timer(30, this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            Color c1 = blend(colors[index], colors[(index + 1) % colors.length], progress);
            Color c2 = blend(colors[(index + 1) % colors.length], colors[(index + 2) % colors.length], progress);
            g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
            g2.fillRect(0, 0, w, h);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            progress += 0.01f;
            if (progress >= 1f) { progress = 0f; index = (index + 1) % colors.length; }
            repaint();
        }

        private Color blend(Color c1, Color c2, float ratio) {
            float r = c1.getRed() + (c2.getRed() - c1.getRed()) * ratio;
            float g = c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio;
            float b = c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio;
            return new Color((int) r, (int) g, (int) b);
        }
    }

    // Dark neon button
    private static class DarkNeonButton extends JButton implements ActionListener {
        private float glowAlpha = 0.3f;
        private boolean increasing = true;
        private final Timer timer;

        DarkNeonButton(String text) {
            super(text);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setOpaque(false);
            timer = new Timer(40, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (increasing) { glowAlpha += 0.02f; if (glowAlpha >= 0.7f) increasing = false; }
            else { glowAlpha -= 0.02f; if (glowAlpha <= 0.3f) increasing = true; }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, new Color(40, 40, 40), 0, h, new Color(10, 10, 10)));
            g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 20, 20));
            g2.setColor(new Color(0, 200, 255, (int) (glowAlpha * 255)));
            g2.setStroke(new BasicStroke(3));
            g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 20, 20));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = (h + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }

    // Hero panel with Heart Rhythm 2D animation
    private static class HeroPanel extends JPanel implements ActionListener {
        private final Timer timer;
        private final List<Integer> points = new ArrayList<>();
        private int t = 0;
        private float glow = 0.5f;
        private boolean increasing = true;

        HeroPanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());
            timer = new Timer(15, this); // Faster updates for smoother animation
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int midY = getHeight() / 2;

            // Neon welcome text
            String welcome = "Welcome to Your Hospital Dashboard";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.setColor(new Color(0, 255, 200, (int)(glow*255)));
            int w = g2.getFontMetrics().stringWidth(welcome);
            g2.drawString(welcome, (getWidth()-w)/2, 60);

            // Description text
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.setColor(new Color(200, 255, 255, 180));
            String desc1 = "Manage patients, doctors, and appointments effortlessly";
            String desc2 = "with futuristic powered dashboard features.";
            g2.drawString(desc1, (getWidth()-g2.getFontMetrics().stringWidth(desc1))/2, 100);
            g2.drawString(desc2, (getWidth()-g2.getFontMetrics().stringWidth(desc2))/2, 130);

            // Heart rhythm line
            int startX = getWidth() / 4;
            int endX = 3 * getWidth() / 4;

            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(0, 255, 255, 200));

            int prevX = startX;
            int prevY = midY;
            for (int i = 0; i < points.size(); i++) {
                int x = startX + i;
                int y = midY - points.get(i);
                g2.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Glow effect
            if(increasing){ glow += 0.02f; if(glow>=0.9f) increasing=false; }
            else { glow -= 0.02f; if(glow<=0.5f) increasing=true; }

            // Heart rhythm pattern
            int value = 0;
            int cycle = t % 100;
            if(cycle < 10) value = 0;
            else if(cycle < 15) value = 30;
            else if(cycle < 20) value = -20;
            else value = 0;

            points.add(value);
            if(points.size() > getWidth() / 2) points.remove(0);

            t++;
            repaint();
        }
    }
}
