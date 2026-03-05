package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class LoginPage extends JFrame {

    private static final long serialVersionUID = 1L;

    private OceanResortSystem mainSystem;
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color ACCENT_COLOR  = new Color(66, 165, 245);

    public LoginPage(OceanResortSystem system) {
        this.mainSystem = system;
        showLoginUI();
    }

    private void showLoginUI() {
        setTitle("Ocean Resort | Login");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        /* ── LEFT PANEL: branding ───────────────────────────────── */
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR,
                        getWidth(), getHeight(), ACCENT_COLOR);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(-50, -50, 200, 200);
                g2.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);
            }
        };
        leftPanel.setLayout(new GridBagLayout());

        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setOpaque(false);

        JLabel logo = new JLabel("🏖️");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("OCEAN RESORT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 200));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("Streamline your resort operations");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(255, 255, 255, 180));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Role hint badges ──────────────────────────────────────
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(makeBadge("👑  Admin"));
        badgeRow.add(makeBadge("👤  Staff"));
        badgeRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandingPanel.add(logo);
        brandingPanel.add(Box.createVerticalStrut(20));
        brandingPanel.add(title);
        brandingPanel.add(Box.createVerticalStrut(10));
        brandingPanel.add(subtitle);
        brandingPanel.add(Box.createVerticalStrut(12));
        brandingPanel.add(desc);
        brandingPanel.add(Box.createVerticalStrut(35));
        brandingPanel.add(badgeRow);
        leftPanel.add(brandingPanel);

        /* ── RIGHT PANEL: login form ────────────────────────────── */
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(350, 460));

        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel("Sign in with your admin or staff account");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernTextField usernameField = new ModernTextField();
        usernameField.setPlaceholder("Enter your username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernPasswordField passwordField = new ModernPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Role indicator — shows after typing username ──────────
        JLabel roleIndicator = new JLabel(" ");
        roleIndicator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Error message label ───────────────────────────────────
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(244, 67, 54));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernButton loginBtn = new ModernButton("LOGIN", PRIMARY_COLOR);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));

        /* ── LOGIN ACTION ──────────────────────────────────────── */
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter username and password.");
                return;
            }

            // validateStaffLogin() queries the DB, stores role+fullName,
            // and returns "Admin", "Staff", or null
            String role = mainSystem.validateStaffLogin(username, password);

            if (role != null) {
                // ✅ Login successful — role already stored in mainSystem
                statusLabel.setText(" ");
                roleIndicator.setText(" ");
                dispose();
                mainSystem.showMainMenu();   // MainMenu reads role from mainSystem

            } else {
                // ❌ Invalid credentials
                usernameField.setError(true);
                roleIndicator.setText(" ");
                statusLabel.setText("❌  Invalid username or password. Please try again.");
                passwordField.setText("");
                usernameField.requestFocus();

                Timer timer = new Timer(2500, evt -> {
                    usernameField.setError(false);
                    statusLabel.setText(" ");
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        ActionListener enterKey = ev -> loginBtn.doClick();
        usernameField.addActionListener(enterKey);
        passwordField.addActionListener(enterKey);

        formPanel.add(welcomeLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(infoLabel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(roleIndicator);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(statusLabel);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(loginBtn);

        rightPanel.add(formPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
        setVisible(true);
    }

    /** Small pill badge for the left branding panel */
    private JLabel makeBadge(String text) {
        JLabel badge = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        return badge;
    }
}
