import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;

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
        JLabel title = new JLabel("Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        // Background panel with animated gradient
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout());
        add(background, BorderLayout.CENTER);

        background.add(topBar, BorderLayout.NORTH);

        // Hero section
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fill(new RoundRectangle2D.Double(20, 15, getWidth() - 40, getHeight() - 30, 32, 32));
            }
        };
        hero.setOpaque(false);
        hero.setLayout(new GridBagLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel heroTitle = new JLabel("Welcome");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heroTitle.setForeground(Color.WHITE);

        JLabel heroDesc = new JLabel("<html><div style='text-align:center;'>Manage patients, view doctors, and book appointments with a modern UI.</div></html>");
        heroDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        heroDesc.setForeground(new Color(235, 235, 235));

        JPanel heroContent = new JPanel();
        heroContent.setOpaque(false);
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.add(centered(heroTitle));
        heroContent.add(Box.createVerticalStrut(6));
        heroContent.add(centered(heroDesc));

        hero.add(heroContent, new GridBagConstraints());
        background.add(hero, BorderLayout.CENTER);

        // Cards row
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 24, 24));
        cardsRow.setOpaque(false);
        cardsRow.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        cardsRow.add(createCard("Manage Patients", e -> new PatientManagement(connection)));
        cardsRow.add(createCard("View Doctors", e -> new DoctorManagement(connection)));
        cardsRow.add(createCard("Book Appointment", e -> new AppointmentManagement(connection)));

        background.add(cardsRow, BorderLayout.SOUTH);

        setVisible(true);
    }

    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(c);
        return p;
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

    // Animated gradient background
    private static class GradientPanel extends JPanel implements ActionListener {
        private final Color[] colors = {
                new Color(0x02, 0x00, 0x24),
                new Color(0x09, 0x09, 0x79),
                new Color(0x00, 0xD4, 0xFF)
        };
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
            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            progress += 0.01f;
            if (progress >= 1f) {
                progress = 0f;
                index = (index + 1) % colors.length;
            }
            repaint();
        }

        private Color blend(Color c1, Color c2, float ratio) {
            float r = c1.getRed() + (c2.getRed() - c1.getRed()) * ratio;
            float g = c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio;
            float b = c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio;
            return new Color((int) r, (int) g, (int) b);
        }
    }

    // Pulsing dark neon button
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
            if (increasing) {
                glowAlpha += 0.02f;
                if (glowAlpha >= 0.7f) increasing = false;
            } else {
                glowAlpha -= 0.02f;
                if (glowAlpha <= 0.3f) increasing = true;
            }
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

    // Main method

}
