package resort;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import javax.swing.Timer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class OceanResortSystem extends JFrame {

    /* ===================== DATA MODEL ===================== */
    static class Reservation {
        String resNo, name, address, contact, roomType;
        LocalDate checkIn, checkOut;
        String status;

        Reservation(String resNo, String name, String address, String contact,
                    String roomType, LocalDate checkIn, LocalDate checkOut) {
            this.resNo = resNo;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.roomType = roomType;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            updateStatus();
        }
        
        void updateStatus() {
            LocalDate today = LocalDate.now();
            if (checkOut.isBefore(today)) {
                status = "Completed";
            } else if (checkIn.isAfter(today)) {
                status = "Upcoming";
            } else if (!checkIn.isAfter(today) && !checkOut.isBefore(today)) {
                status = "Active";
            } else {
                status = "Upcoming";
            }
        }
    }

    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static final String FILE_NAME = "reservations.txt";

    /* ===================== MODERN THEME ===================== */
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color ACCENT_COLOR = new Color(66, 165, 245);
    private final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private final Color WARNING_COLOR = new Color(255, 152, 0);
    private final Color DANGER_COLOR = new Color(244, 67, 54);
    private final Color INFO_COLOR = new Color(41, 182, 246);
    private final Color SIDEBAR_COLOR = new Color(38, 50, 56);
    private final Color SIDEBAR_HOVER = new Color(55, 71, 79);
    private final Color BG_COLOR = new Color(250, 250, 250);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JPanel contentArea;
    private String currentSection = "Dashboard";
    private HashMap<String, JButton> menuButtons = new HashMap<>();

    public OceanResortSystem() {
        loadReservationsFromFile();
        showLoginUI();
    }

    /* ===================== CUSTOM MODERN COMPONENTS ===================== */
    class ModernButton extends JButton {
        private Color baseColor;
        private boolean isIconButton = false;
        
        public ModernButton(String text) {
            this(text, PRIMARY_COLOR);
        }
        
        public ModernButton(String text, Color color) {
            super(text);
            this.baseColor = color;
            setupButton();
        }
        
        public ModernButton(String text, Color color, boolean iconButton) {
            super(text);
            this.baseColor = color;
            this.isIconButton = iconButton;
            setupButton();
        }
        
        private void setupButton() {
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (!isIconButton) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2, 12, 12));
            }
            
            if (getModel().isPressed()) {
                g2.setColor(darken(baseColor, 0.2f));
            } else if (getModel().isRollover()) {
                g2.setColor(brighten(baseColor, 0.15f));
            } else {
                g2.setColor(baseColor);
            }
            
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - (isIconButton ? 0 : 2), 
                                                getHeight() - (isIconButton ? 0 : 2), 12, 12));
            super.paintComponent(g);
            g2.dispose();
        }
        
        private Color brighten(Color color, float factor) {
            int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
            int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
            int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
            return new Color(r, g, b);
        }
        
        private Color darken(Color color, float factor) {
            int r = (int)(color.getRed() * (1 - factor));
            int g = (int)(color.getGreen() * (1 - factor));
            int b = (int)(color.getBlue() * (1 - factor));
            return new Color(r, g, b);
        }
    }

    class ModernTextField extends JTextField {
        private String placeholder = "";
        private boolean hasError = false;
        
        public ModernTextField() {
            setOpaque(false);
            setFont(MAIN_FONT);
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        }
        
        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }
        
        public void setError(boolean error) {
            this.hasError = error;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            
            if (hasError) {
                g2.setColor(DANGER_COLOR);
                g2.setStroke(new BasicStroke(2));
            } else if (hasFocus()) {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(224, 224, 224));
                g2.setStroke(new BasicStroke(1));
            }
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 10, 10));
            
            super.paintComponent(g);
            
            if (getText().isEmpty() && !placeholder.isEmpty() && !hasFocus()) {
                g2.setColor(new Color(158, 158, 158));
                g2.setFont(getFont());
                g2.drawString(placeholder, 16, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
            }
            
            g2.dispose();
        }
    }

    class ModernPasswordField extends JPasswordField {
        private boolean showPassword = false;
        private JButton toggleButton;
        
        public ModernPasswordField() {
            setOpaque(false);
            setFont(MAIN_FONT);
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 50));
            
            toggleButton = new JButton("👁");
            toggleButton.setBorderPainted(false);
            toggleButton.setContentAreaFilled(false);
            toggleButton.setFocusPainted(false);
            toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            toggleButton.addActionListener(e -> {
                showPassword = !showPassword;
                setEchoChar(showPassword ? '\u0000' : '●');
            });
            
            setLayout(new BorderLayout());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(toggleButton);
            add(buttonPanel, BorderLayout.EAST);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            
            if (hasFocus()) {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(new Color(224, 224, 224));
                g2.setStroke(new BasicStroke(1));
            }
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 10, 10));
            
            super.paintComponent(g);
            g2.dispose();
        }
    }

    /* ===================== LOGIN UI ===================== */
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
                showMainMenu();
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

    /* ===================== MAIN MENU ===================== */
    private void showMainMenu() {
        setTitle("Ocean Resort Management System");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        
        JPanel main = new JPanel(new BorderLayout());
        
        // Modern Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));
        
        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(260, 100));
        logoPanel.setBorder(new EmptyBorder(0, 10, 20, 10));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoIcon = new JLabel("🏖️", JLabel.CENTER);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoText = new JLabel("OCEAN RESORT");
        logoText.setForeground(Color.WHITE);
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(logoText);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(30));
        
        // Menu items
        String[][] menuItems = {
            {"📊", "Dashboard"},
            {"➕", "Add Reservation"},
            {"📋", "View Reservations"},
            {"💳", "Billing"},
            {"📈", "Analytics"},
            {"❓", "Help"},
            {"🚪", "Logout"}
        };
        
        Dimension btnMaxSize = new Dimension(230, 45);
        
        for (String[] item : menuItems) {
            JButton btn = createModernSidebarButton(item[0], item[1]);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(btnMaxSize);
            menuButtons.put(item[1], btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        main.add(sidebar, BorderLayout.WEST);
        add(main);
        
        // User info
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(230, 50));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel("Admin User");
        userName.setForeground(Color.WHITE);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel userRole = new JLabel("Administrator");
        userRole.setForeground(new Color(255, 255, 255, 150));
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userInfo.add(userName);
        userInfo.add(userRole);
        
        userPanel.add(userIcon, BorderLayout.WEST);
        userPanel.add(userInfo, BorderLayout.CENTER);
        sidebar.add(userPanel);
        
        // Content area
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_COLOR);
        
        main.add(sidebar, BorderLayout.WEST);
        main.add(contentArea, BorderLayout.CENTER);
        
        setContentPane(main);
        navigateTo("Dashboard");
        revalidate();
    }
    
    private JButton createModernSidebarButton(String icon, String text) {
        JButton btn = new JButton("  " + icon + "   " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(255, 255, 255, 200));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setMaximumSize(new Dimension(230, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!text.equals(currentSection)) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!text.equals(currentSection)) {
                    btn.setBackground(SIDEBAR_COLOR);
                }
            }
        });
        
        btn.addActionListener(e -> navigateTo(text));
        
        return btn;
    }

    private void navigateTo(String section) {
        if (menuButtons.containsKey(currentSection)) {
            menuButtons.get(currentSection).setBackground(SIDEBAR_COLOR);
            menuButtons.get(currentSection).setForeground(new Color(255, 255, 255, 200));
        }
        
        currentSection = section;
        
        if (menuButtons.containsKey(section)) {
            menuButtons.get(section).setBackground(PRIMARY_COLOR);
            menuButtons.get(section).setForeground(Color.WHITE);
        }
        
        contentArea.removeAll();
        
        switch (section) {
            case "Dashboard":
                showDashboard();
                break;
            case "Add Reservation":
                showAddReservation();
                break;
            case "View Reservations":
                showViewReservations();
                break;
            case "Billing":
                showBilling();
                break;
            case "Analytics":
                showAnalytics();
                break;
            case "Help":
                showHelp();
                break;
            case "Logout":
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", 
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new OceanResortSystem();
                }
                return;
        }
        
        contentArea.revalidate();
        contentArea.repaint();
    }

    /* ===================== DASHBOARD ===================== */
    private void showDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel header = new JLabel("Dashboard");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subheader = new JLabel("Welcome back! Here's what's happening today.");
        subheader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheader.setForeground(Color.GRAY);
        subheader.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(header);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subheader);
        
        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);

        // Stats Cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(25, 0, 25, 0));

        int totalReservations = reservations.size();
        int activeReservations = 0;
        int upcomingReservations = 0;
        long totalRevenue = 0;

        LocalDate today = LocalDate.now();
        for (Reservation r : reservations.values()) {
            r.updateStatus();
            if (r.status.equals("Active")) activeReservations++;
            if (r.status.equals("Upcoming")) upcomingReservations++;

            long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
            int rate = getRoomRate(r.roomType);
            totalRevenue += nights * rate;
        }

        statsGrid.add(createModernStatCard("Total Bookings", String.valueOf(totalReservations), "📊", INFO_COLOR));
        statsGrid.add(createModernStatCard("Active Guests", String.valueOf(activeReservations), "✅", SUCCESS_COLOR));
        statsGrid.add(createModernStatCard("Upcoming", String.valueOf(upcomingReservations), "📅", WARNING_COLOR));
        statsGrid.add(createModernStatCard("Total Revenue", "LKR " + String.format("%,d", totalRevenue), "💰", new Color(156, 39, 176)));

        // Bottom section
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSection.setOpaque(false);

        bottomSection.add(createRecentBookingsPanel());
        bottomSection.add(createRoomStatsPanel());

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(statsGrid, BorderLayout.NORTH);
        content.add(bottomSection, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    private JPanel createModernStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(117, 117, 117));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(valueLabel);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        
        card.add(topPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRecentBookingsPanel() {
        JPanel panel = createModernCard("Recent Bookings", "📋");
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        ArrayList<Reservation> sortedRes = new ArrayList<>(reservations.values());
        sortedRes.sort((a, b) -> b.checkIn.compareTo(a.checkIn));

        int count = 0;
        for (Reservation r : sortedRes) {
            if (count >= 6) break;
            
            JPanel item = new JPanel(new BorderLayout(12, 0));
            item.setOpaque(false);
            item.setBorder(new EmptyBorder(12, 0, 12, 0));
            
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(r.name);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel roomLabel = new JLabel(r.roomType + " Room • " + r.checkIn);
            roomLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            roomLabel.setForeground(Color.GRAY);
            roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            leftPanel.add(nameLabel);
            leftPanel.add(Box.createVerticalStrut(3));
            leftPanel.add(roomLabel);
            
            JLabel statusLabel = new JLabel(r.status);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            statusLabel.setOpaque(true);
            statusLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
            
            Color statusColor;
            if (r.status.equals("Active")) {
                statusColor = SUCCESS_COLOR;
            } else if (r.status.equals("Upcoming")) {
                statusColor = WARNING_COLOR;
            } else {
                statusColor = Color.GRAY;
            }
            
            statusLabel.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), 
                                               statusColor.getBlue(), 30));
            statusLabel.setForeground(statusColor);
            
            item.add(leftPanel, BorderLayout.CENTER);
            item.add(statusLabel, BorderLayout.EAST);
            listPanel.add(item);
            
            if (count < sortedRes.size() - 1 && count < 5) {
                JSeparator sep = new JSeparator();
                listPanel.add(sep);
            }
            count++;
        }

        if (reservations.isEmpty()) {
            JLabel emptyLabel = new JLabel("No recent bookings", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.LIGHT_GRAY);
            listPanel.add(emptyLabel);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomStatsPanel() {
        JPanel panel = createModernCard("Room Distribution", "🏠");
        
        Map<String, Integer> roomTypeCount = new HashMap<>();
        roomTypeCount.put("Single", 0);
        roomTypeCount.put("Double", 0);
        roomTypeCount.put("Suite", 0);
        
        for (Reservation r : reservations.values()) {
            roomTypeCount.put(r.roomType, roomTypeCount.get(r.roomType) + 1);
        }
        
        int total = reservations.size();
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        statsPanel.add(createProgressBar("Single Room", roomTypeCount.get("Single"), total, INFO_COLOR));
        statsPanel.add(createProgressBar("Double Room", roomTypeCount.get("Double"), total, SUCCESS_COLOR));
        statsPanel.add(createProgressBar("Suite", roomTypeCount.get("Suite"), total, new Color(156, 39, 176)));

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createModernCard(String title, String icon) {
        JPanel card = new JPanel(new BorderLayout(0, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon + "  ");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        card.add(headerPanel, BorderLayout.NORTH);
        return card;
    }

    private JPanel createProgressBar(String label, int count, int total, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 8));
        panel.setOpaque(false);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel countLabel = new JLabel(count + " bookings");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setForeground(color);

        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(countLabel, BorderLayout.EAST);

        final int percentage = total > 0 ? (int) ((count * 100.0) / total) : 0;
        
        JPanel progressBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(240, 240, 240));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), 12, 12, 12));
                
                if (total > 0) {
                    int barWidth = (int) (getWidth() * percentage / 100.0);
                    g2.setColor(color);
                    g2.fill(new RoundRectangle2D.Double(0, 0, barWidth, 12, 12, 12));
                }
            }
        };
        progressBar.setOpaque(false);
        progressBar.setPreferredSize(new Dimension(0, 12));

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        return panel;
    }

    /* ===================== ADD RESERVATION ===================== */
    private void showAddReservation() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        JLabel header = new JLabel("New Reservation");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));
        
        JPanel formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(35, 40, 35, 40));
        formCard.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        ModernTextField resNo = new ModernTextField();
        resNo.setPlaceholder("e.g., RES-001");
        ModernTextField name = new ModernTextField();
        name.setPlaceholder("Guest full name");
        ModernTextField addr = new ModernTextField();
        addr.setPlaceholder("Guest address");
        ModernTextField cont = new ModernTextField();
        cont.setPlaceholder("+94 XX XXX XXXX");
        
        JComboBox<String> room = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        room.setFont(MAIN_FONT);
        room.setBackground(Color.WHITE);
        
        SpinnerDateModel checkInModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkInSpinner = new JSpinner(checkInModel);
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkInSpinner.setFont(MAIN_FONT);
        
        SpinnerDateModel checkOutModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkOutSpinner = new JSpinner(checkOutModel);
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setFont(MAIN_FONT);
        
        int row = 0;
        addFormRow(formCard, gbc, "Reservation Number *", resNo, row++);
        addFormRow(formCard, gbc, "Guest Full Name *", name, row++);
        addFormRow(formCard, gbc, "Address *", addr, row++);
        addFormRow(formCard, gbc, "Contact Number *", cont, row++);
        addFormRow(formCard, gbc, "Room Type *", room, row++);
        addFormRow(formCard, gbc, "Check-In Date *", checkInSpinner, row++);
        addFormRow(formCard, gbc, "Check-Out Date *", checkOutSpinner, row++);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        
        ModernButton clearBtn = new ModernButton("CLEAR", new Color(117, 117, 117));
        clearBtn.setPreferredSize(new Dimension(120, 46));
        clearBtn.addActionListener(e -> {
            resNo.setText("");
            name.setText("");
            addr.setText("");
            cont.setText("");
            room.setSelectedIndex(0);
            checkInSpinner.setValue(new Date());
            checkOutSpinner.setValue(new Date());
        });
        
        ModernButton saveBtn = new ModernButton("SAVE RESERVATION", SUCCESS_COLOR);
        saveBtn.setPreferredSize(new Dimension(180, 46));
        saveBtn.addActionListener(e -> {
            boolean valid = true;
            if (resNo.getText().trim().isEmpty()) { resNo.setError(true); valid = false; }
            if (name.getText().trim().isEmpty()) { name.setError(true); valid = false; }
            if (addr.getText().trim().isEmpty()) { addr.setError(true); valid = false; }
            if (cont.getText().trim().isEmpty()) { cont.setError(true); valid = false; }
            
            if (!valid) {
                JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Date inDate = (Date) checkInSpinner.getValue();
                Date outDate = (Date) checkOutSpinner.getValue();

                LocalDate checkIn = inDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOut = outDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(this, "Check-out must be after check-in!", "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (reservations.containsKey(resNo.getText())) {
                    JOptionPane.showMessageDialog(this, "Reservation number already exists!", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                reservations.put(resNo.getText(), new Reservation(
                    resNo.getText(), name.getText(), addr.getText(), cont.getText(),
                    room.getSelectedItem().toString(), checkIn, checkOut
                ));

                saveReservationsToFile();
                JOptionPane.showMessageDialog(this, "✓ Reservation saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearBtn.doClick();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving reservation!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 8, 8, 8);
        formCard.add(buttonPanel, gbc);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setOpaque(false);
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(formCard, BorderLayout.CENTER);
        
        panel.add(contentPanel);
        contentArea.add(panel);
    }
    
    private void addFormRow(JPanel container, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        container.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        component.setPreferredSize(new Dimension(400, 50));
        container.add(component, gbc);
    }

    /* ===================== VIEW RESERVATIONS ===================== */
    private void showViewReservations() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel header = new JLabel("All Reservations");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        ModernTextField searchField = new ModernTextField();
        searchField.setPlaceholder("Search reservations...");
        searchField.setPreferredSize(new Dimension(300, 45));
        
        ModernButton refreshBtn = new ModernButton("REFRESH", INFO_COLOR, true);
        refreshBtn.setPreferredSize(new Dimension(120, 45));
        
        searchPanel.add(searchField);
        searchPanel.add(refreshBtn);
        
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Table
        String[] columns = {"ID", "Guest Name", "Room", "Check-In", "Check-Out", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(60);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), 
                                                PRIMARY_COLOR.getBlue(), 40));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(new Color(66, 66, 66));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getPreferredSize().width, 50));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        
        // Status column renderer
        table.getColumn("Status").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(6, 12, 6, 12));
            
            Color statusColor;
            String status = value.toString();
            if (status.equals("Active")) {
                statusColor = SUCCESS_COLOR;
            } else if (status.equals("Upcoming")) {
                statusColor = WARNING_COLOR;
            } else if (status.equals("Completed")) {
                statusColor = new Color(158, 158, 158);
            } else {
                statusColor = Color.GRAY;
            }
            
            label.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), 
                                         statusColor.getBlue(), 25));
            label.setForeground(statusColor);
            
            return label;
        });
        
        // Actions column
        table.getColumn("Actions").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
            panel1.setOpaque(false);
            
            Dimension btnSize = new Dimension(55, 40); 
            Font btnFont = new Font("Segoe UI Emoji", Font.PLAIN, 20);

            JButton viewBtn = new JButton("👁");
            viewBtn.setPreferredSize(btnSize); 
            viewBtn.setFont(btnFont);         
            viewBtn.setToolTipText("View Details");
            styleActionButton(viewBtn, INFO_COLOR);

            JButton deleteBtn = new JButton("🗑");
            deleteBtn.setPreferredSize(btnSize);
            deleteBtn.setFont(btnFont);
            deleteBtn.setToolTipText("Delete");
            styleActionButton(deleteBtn, DANGER_COLOR);
            
            panel1.add(viewBtn);
            panel1.add(deleteBtn);
            return panel1;
        });
        
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object value, boolean isSelected, int row, int column) {
                JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
                panel1.setOpaque(false);
                
                String resNo = (String) tbl.getValueAt(row, 0);
                
                Dimension btnSize = new Dimension(55, 40);
                Font btnFont = new Font("Segoe UI Emoji", Font.PLAIN, 20);

                JButton viewBtn = new JButton("👁");
                viewBtn.setPreferredSize(btnSize);
                viewBtn.setFont(btnFont);
                viewBtn.setToolTipText("View Details");
                styleActionButton(viewBtn, INFO_COLOR);
                viewBtn.addActionListener(e -> showReservationDetails(resNo));

                JButton deleteBtn = new JButton("🗑");
                deleteBtn.setPreferredSize(btnSize);
                deleteBtn.setFont(btnFont);
                deleteBtn.setToolTipText("Delete");
                styleActionButton(deleteBtn, DANGER_COLOR);
                
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        OceanResortSystem.this,
                        "Delete this reservation?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        reservations.remove(resNo);
                        saveReservationsToFile();
                        refreshTable(model, null);
                        JOptionPane.showMessageDialog(OceanResortSystem.this, "✓ Deleted successfully!");
                    }
                });
                
                panel1.add(viewBtn);
                panel1.add(deleteBtn);
                return panel1;
            }
        });
        
        refreshTable(model, null);
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String term = searchField.getText().toLowerCase().trim();
                refreshTable(model, term.isEmpty() ? null : term);
            }
        });
        
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable(model, null);
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel);
        contentArea.add(panel);
    }
    
    private void styleActionButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        btn.setForeground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(38, 35));
    }
    
    private void refreshTable(DefaultTableModel model, String filter) {
        model.setRowCount(0);
        for (Reservation r : reservations.values()) {
            r.updateStatus();
            if (filter == null || 
                r.name.toLowerCase().contains(filter) || 
                r.resNo.toLowerCase().contains(filter) ||
                r.contact.toLowerCase().contains(filter)) {
                model.addRow(new Object[]{
                    r.resNo, r.name, r.roomType, r.checkIn, r.checkOut, r.status, ""
                });
            }
        }
    }
    
    private void showReservationDetails(String resNo) {
        Reservation r = reservations.get(resNo);
        if (r == null) return;
        
        JDialog dialog = new JDialog(this, "Reservation Details", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(35, 35, 35, 35));
        
        JLabel titleLabel = new JLabel("Reservation Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JPanel detailsPanel = new JPanel(new GridLayout(9, 2, 18, 18));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        addDetailRow(detailsPanel, "Reservation No:", r.resNo);
        addDetailRow(detailsPanel, "Guest Name:", r.name);
        addDetailRow(detailsPanel, "Address:", r.address);
        addDetailRow(detailsPanel, "Contact:", r.contact);
        addDetailRow(detailsPanel, "Room Type:", r.roomType);
        addDetailRow(detailsPanel, "Check-In:", r.checkIn.toString());
        addDetailRow(detailsPanel, "Check-Out:", r.checkOut.toString());
        addDetailRow(detailsPanel, "Status:", r.status);
        
        long nights = ChronoUnit.DAYS.between(r.checkIn, r.checkOut);
        int rate = getRoomRate(r.roomType);
        long total = nights * rate;
        addDetailRow(detailsPanel, "Total Amount:", "LKR " + String.format("%,d", total));
        
        ModernButton closeBtn = new ModernButton("CLOSE", new Color(117, 117, 117));
        closeBtn.setPreferredSize(new Dimension(0, 48));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(closeBtn, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLabel.setForeground(new Color(117, 117, 117));
        labelPanel.add(lblLabel);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setOpaque(false);
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valuePanel.add(valLabel);
        
        panel.add(labelPanel);
        panel.add(valuePanel);
    }

    /* ===================== BILLING ===================== */
    private void showBilling() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel header = new JLabel("Billing & Invoices");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));

        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        ModernTextField billInput = new ModernTextField();
        billInput.setPlaceholder("Enter Reservation ID");
        billInput.setPreferredSize(new Dimension(0, 50));
        
        ModernButton genBtn = new ModernButton("GENERATE INVOICE", PRIMARY_COLOR);
        genBtn.setPreferredSize(new Dimension(190, 50));

        searchPanel.add(billInput, BorderLayout.CENTER);
        searchPanel.add(genBtn, BorderLayout.EAST);

        JEditorPane invoicePane = new JEditorPane("text/html", "");
        invoicePane.setEditable(false);
        invoicePane.setBackground(Color.WHITE);
        invoicePane.setText(getEmptyInvoiceHTML());

        genBtn.addActionListener(e -> {
            Reservation r = reservations.get(billInput.getText());
            if (r != null) {
                invoicePane.setText(generateModernInvoiceHTML(r));
            } else {
                JOptionPane.showMessageDialog(this, "Reservation not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JScrollPane scroll = new JScrollPane(invoicePane);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JPanel invoiceContainer = new JPanel(new BorderLayout());
        invoiceContainer.setOpaque(false);
        invoiceContainer.setBorder(new EmptyBorder(20, 0, 0, 0));
        invoiceContainer.add(scroll);
        
        panel.add(header, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(invoiceContainer, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        contentArea.add(panel);
    }
    
    private String getEmptyInvoiceHTML() {
        return "<html><body style='text-align:center; padding:150px 50px; font-family:Segoe UI; color:#aaa; background:#fafafa;'>"
                + "<h1 style='color:#ccc; font-size:48px;'>📄</h1>"
                + "<h2>No Invoice Generated</h2>"
                + "<p>Enter a reservation ID and click 'Generate Invoice'</p>"
                + "</body></html>";
    }
    
    private String generateModernInvoiceHTML(Reservation r) {
        long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
        int rate = getRoomRate(r.roomType);
        long total = nights * rate;
        
        return "<html><body style='font-family:Segoe UI; margin:0; padding:50px; background:#f5f5f5;'>"
                + "<div style='max-width:800px; margin:0 auto; background:white; padding:50px; border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,0.1);'>"
                + "<div style='text-align:center; margin-bottom:40px;'>"
                + "<h1 style='color:#1976D2; margin:0; font-size:36px;'>🏖️ OCEAN RESORT</h1>"
                + "<p style='color:#666; margin:5px 0;'>Luxury Beachfront Experience</p>"
                + "</div>"
                + "<div style='background:#1976D2; color:white; padding:20px; border-radius:8px; margin-bottom:30px;'>"
                + "<h2 style='margin:0;'>INVOICE</h2>"
                + "<p style='margin:5px 0; opacity:0.9;'>Invoice #" + r.resNo + "</p>"
                + "<p style='margin:5px 0; opacity:0.9;'>Date: " + LocalDate.now() + "</p>"
                + "</div>"
                + "<table style='width:100%; margin-bottom:40px;'><tr>"
                + "<td style='vertical-align:top;'><p style='color:#999; margin:0; font-size:12px;'>BILLED TO</p>"
                + "<h3 style='margin:10px 0; color:#333;'>" + r.name + "</h3>"
                + "<p style='margin:5px 0; color:#666;'>" + r.address + "</p>"
                + "<p style='margin:5px 0; color:#666;'>" + r.contact + "</p></td>"
                + "<td style='text-align:right; vertical-align:top;'><p style='color:#999; margin:0; font-size:12px;'>STAY DETAILS</p>"
                + "<p style='margin:10px 0; color:#333;'><b>Room:</b> " + r.roomType + "</p>"
                + "<p style='margin:5px 0; color:#333;'><b>Check-In:</b> " + r.checkIn + "</p>"
                + "<p style='margin:5px 0; color:#333;'><b>Check-Out:</b> " + r.checkOut + "</p>"
                + "<p style='margin:5px 0; color:#333;'><b>Duration:</b> " + nights + " Night(s)</p></td>"
                + "</tr></table>"
                + "<table style='width:100%; border-collapse:collapse; margin-bottom:30px;'>"
                + "<thead><tr style='background:#f5f5f5;'>"
                + "<th style='padding:15px; text-align:left; border-bottom:2px solid #1976D2;'>Description</th>"
                + "<th style='padding:15px; text-align:center; border-bottom:2px solid #1976D2;'>Nights</th>"
                + "<th style='padding:15px; text-align:right; border-bottom:2px solid #1976D2;'>Rate</th>"
                + "<th style='padding:15px; text-align:right; border-bottom:2px solid #1976D2;'>Amount</th>"
                + "</tr></thead><tbody>"
                + "<tr><td style='padding:15px; border-bottom:1px solid #eee;'>Accommodation<br><small style='color:#999;'>" + r.roomType + " Room</small></td>"
                + "<td style='padding:15px; text-align:center; border-bottom:1px solid #eee;'>" + nights + "</td>"
                + "<td style='padding:15px; text-align:right; border-bottom:1px solid #eee;'>LKR " + String.format("%,d", rate) + "</td>"
                + "<td style='padding:15px; text-align:right; border-bottom:1px solid #eee;'><b>LKR " + String.format("%,d", total) + "</b></td>"
                + "</tr></tbody></table>"
                + "<div style='text-align:right; padding:25px; background:#f9f9f9; border-radius:8px;'>"
                + "<p style='color:#666; margin:0; font-size:14px;'>TOTAL AMOUNT</p>"
                + "<h1 style='color:#1976D2; margin:10px 0 0 0; font-size:36px;'>LKR " + String.format("%,d", total) + "</h1>"
                + "</div>"
                + "<p style='text-align:center; margin-top:40px; font-size:12px; color:#999;'>"
                + "Thank you for choosing Ocean Resort! Questions? Call +94 11 234 5678"
                + "</p></div></body></html>";
    }

    /* ===================== ANALYTICS ===================== */
    private void showAnalytics() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel header = new JLabel("Analytics & Reports");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        Map<String, Long> revenueByRoom = new HashMap<>();
        revenueByRoom.put("Single", 0L);
        revenueByRoom.put("Double", 0L);
        revenueByRoom.put("Suite", 0L);
        
        for (Reservation r : reservations.values()) {
            long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
            int rate = getRoomRate(r.roomType);
            revenueByRoom.put(r.roomType, revenueByRoom.get(r.roomType) + (nights * rate));
        }

        statsPanel.add(createAnalyticsCard("Single Room Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Single")), INFO_COLOR));
        statsPanel.add(createAnalyticsCard("Double Room Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Double")), SUCCESS_COLOR));
        statsPanel.add(createAnalyticsCard("Suite Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Suite")), new Color(156, 39, 176)));
        statsPanel.add(createAnalyticsCard("Average Booking Duration", 
            calculateAverageDuration() + " nights", WARNING_COLOR));

        panel.add(header, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        contentArea.add(panel);
    }
    
    private JPanel createAnalyticsCard(String title, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 16, 16));
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(117, 117, 117));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
    
    private String calculateAverageDuration() {
        if (reservations.isEmpty()) return "0";
        long totalNights = 0;
        for (Reservation r : reservations.values()) {
            totalNights += ChronoUnit.DAYS.between(r.checkIn, r.checkOut);
        }
        return String.format("%.1f", (double) totalNights / reservations.size());
    }

    /* ===================== HELP ===================== */
    private void showHelp() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        JLabel header = new JLabel("Help & Documentation");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));
        
        JEditorPane helpPane = new JEditorPane("text/html", "");
        helpPane.setEditable(false);

        String helpContent = "<html><body style='font-family:Segoe UI; padding:30px; line-height:1.8; background:white;'>"
                + "<div style='background:linear-gradient(135deg, #1976D2 0%, #42A5F5 100%); color:white; padding:30px; border-radius:12px; margin-bottom:30px;'>"
                + "<h1 style='margin:0; font-size:32px;'>🏖️ Ocean Resort Management System</h1>"
                + "<p style='margin:10px 0 0 0; opacity:0.95;'>Complete Staff Operations Manual v2.0</p>"
                + "</div>"
                
                + "<div style='background:#f5f5f5; padding:25px; border-radius:10px; margin-bottom:20px;'>"
                + "<h2 style='color:#1976D2; margin:0 0 15px 0;'>📋 Room Rates & Categories</h2>"
                + "<table style='width:100%; border-collapse:collapse;'>"
                + "<tr style='background:#1976D2; color:white;'>"
                + "<th style='padding:12px; text-align:left;'>Room Type</th>"
                + "<th style='padding:12px; text-align:left;'>Rate/Night</th>"
                + "<th style='padding:12px; text-align:left;'>Capacity</th>"
                + "<th style='padding:12px; text-align:left;'>Amenities</th></tr>"
                + "<tr style='background:white;'><td style='padding:12px; border-bottom:1px solid #e0e0e0;'>🛏 Single</td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'><b>LKR 8,000</b></td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'>1 Adult</td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'>Breakfast, WiFi</td></tr>"
                + "<tr style='background:white;'><td style='padding:12px; border-bottom:1px solid #e0e0e0;'>🛏🛏 Double</td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'><b>LKR 12,000</b></td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'>2 Adults</td>"
                + "<td style='padding:12px; border-bottom:1px solid #e0e0e0;'>Breakfast, WiFi, Sea View</td></tr>"
                + "<tr style='background:white;'><td style='padding:12px;'>🏠 Suite</td>"
                + "<td style='padding:12px;'><b>LKR 20,000</b></td>"
                + "<td style='padding:12px;'>4 Adults</td>"
                + "<td style='padding:12px;'>Full Board, Pool, Spa</td></tr>"
                + "</table></div>"
                
                + "<div style='background:white; padding:25px; border-radius:10px; margin-bottom:20px; border-left:4px solid #4CAF50;'>"
                + "<h2 style='color:#4CAF50; margin:0 0 15px 0;'>✅ System Navigation</h2>"
                + "<ul style='padding-left:20px;'>"
                + "<li><b>Dashboard:</b> Overview of all operations and statistics</li>"
                + "<li><b>Add Reservation:</b> Create new guest bookings</li>"
                + "<li><b>View Reservations:</b> Search, view, and manage bookings</li>"
                + "<li><b>Billing:</b> Generate professional invoices</li>"
                + "<li><b>Analytics:</b> Revenue reports and insights</li>"
                + "</ul></div>"
                
                + "<div style='background:#FFF3E0; padding:20px; border-radius:10px; border-left:4px solid #FF9800;'>"
                + "<h3 style='margin:0 0 10px 0; color:#F57C00;'>📞 Support Contacts</h3>"
                + "<p style='margin:5px 0;'><b>IT Support:</b> Ext 404 | support@oceanresort.com</p>"
                + "<p style='margin:5px 0;'><b>Manager:</b> Ext 101 | manager@oceanresort.com</p>"
                + "<p style='margin:5px 0;'><b>Reception:</b> Ext 100 | front-desk@oceanresort.com</p>"
                + "</div>"
                
                + "</body></html>";

        helpPane.setText(helpContent);
        
        JScrollPane scroll = new JScrollPane(helpPane);
        scroll.setBorder(null);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);
        
        panel.add(contentPanel);
        contentArea.add(panel);
    }

    /* ===================== UTILITIES ===================== */
    private int getRoomRate(String roomType) {
        switch(roomType) {
            case "Single": return 8000;
            case "Double": return 12000;
            case "Suite": return 20000;
            default: return 0;
        }
    }

    /* ===================== FILE HANDLING ===================== */
    private void saveReservationsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation r : reservations.values()) {
                bw.write(r.resNo + "," + r.name + "," + r.address + "," + r.contact + "," + 
                         r.roomType + "," + r.checkIn + "," + r.checkOut);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReservationsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 7) {
                    Reservation r = new Reservation(
                        data[0], data[1], data[2], data[3], data[4],
                        LocalDate.parse(data[5]), LocalDate.parse(data[6])
                    );
                    reservations.put(r.resNo, r);
                }
            }
        } catch (IOException e) {
            // File doesn't exist on first run
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new OceanResortSystem());
    }
}