package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class LoginPage extends JFrame {
    private OceanResortSystem mainSystem;
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color ACCENT_COLOR = new Color(66, 165, 245);

    public LoginPage(OceanResortSystem system) {
        this.mainSystem = system;
        showLoginUI();
    }

    private void showLoginUI() {
        setTitle("Ocean Resort | Staff Login");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // Left panel with gradient
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), getHeight(), ACCENT_COLOR
                );
                g2.setPaint(gradient);
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
        
        JLabel logoLabel = new JLabel("🏖️");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 90));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("OCEAN RESORT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Streamline your resort operations");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(255, 255, 255, 180));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        brandingPanel.add(logoLabel);
        brandingPanel.add(Box.createVerticalStrut(20));
        brandingPanel.add(titleLabel);
        brandingPanel.add(Box.createVerticalStrut(10));
        brandingPanel.add(subtitleLabel);
        brandingPanel.add(Box.createVerticalStrut(20));
        brandingPanel.add(descLabel);
        
        leftPanel.add(brandingPanel);
        
        // Right panel - Login form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(350, 400));
        
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Sign in to continue to your dashboard");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
        
        ModernButton loginBtn = new ModernButton("LOGIN", PRIMARY_COLOR);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.equals("admin") && password.equals("admin123")) {
                dispose();
                mainSystem.showMainMenu();
            } else {
                usernameField.setError(true);
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocus();
                Timer timer = new Timer(2000, evt -> usernameField.setError(false));
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        ActionListener loginAction = e -> loginBtn.doClick();
        usernameField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
        
        formPanel.add(welcomeLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(instructionLabel);
        formPanel.add(Box.createVerticalStrut(40));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(loginBtn);
        
        rightPanel.add(formPanel);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
}
