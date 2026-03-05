package resort;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;

    private OceanResortSystem mainSystem;
    private JPanel contentArea;
    private String currentSection = "Dashboard";
    private HashMap<String, JButton> menuButtons = new HashMap<>();

    private final Color PRIMARY_COLOR;
    private final Color SIDEBAR_COLOR;
    private final Color SIDEBAR_HOVER;
    private final Color BG_COLOR;

    public MainMenu(OceanResortSystem system) {
        this.mainSystem   = system;
        this.PRIMARY_COLOR = system.PRIMARY_COLOR;
        this.SIDEBAR_COLOR = system.SIDEBAR_COLOR;
        this.SIDEBAR_HOVER = system.SIDEBAR_HOVER;
        this.BG_COLOR      = system.BG_COLOR;
        showMainMenu();
    }

    private void showMainMenu() {

        // ── Read logged-in user info ──────────────────────────────
        String role     = mainSystem.getLoggedInRole();       // "Admin" or "Staff"
        String fullName = mainSystem.getLoggedInFullName();   // e.g. "Kasun Perera"
        boolean isAdmin = "Admin".equalsIgnoreCase(role);

        setTitle("Ocean Resort Management System  —  " + role + ": " + fullName);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());

        /* ── SIDEBAR ─────────────────────────────────────────────── */
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

        // ── Role badge ───────────────────────────────────────────
        JLabel roleBadge = new JLabel(
                isAdmin ? "👑  ADMINISTRATOR" : "👤  STAFF MEMBER", JLabel.CENTER);
        roleBadge.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        roleBadge.setForeground(isAdmin
                ? new Color(144, 202, 249)    // light blue  for Admin
                : new Color(165, 214, 167));   // light green for Staff
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleBadge.setMaximumSize(new Dimension(230, 26));
        sidebar.add(roleBadge);
        sidebar.add(Box.createVerticalStrut(18));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setMaximumSize(new Dimension(220, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(18));

        /* ── MENU ITEMS ──────────────────────────────────────────
           Rule:  "Manage Staff" appears ONLY when the logged-in
                  user is an Admin.  Staff users never see it.
        ─────────────────────────────────────────────────────── */
        String[][] allMenuItems = {
            {"📊", "Dashboard"},
            {"➕", "Add Reservation"},
            {"📋", "View Reservations"},
            {"💳", "Billing"},
            {"📈", "Analytics"},
            {"👥", "Manage Staff"},    // ← Admin only
            {"❓", "Help"},
            {"🚪", "Logout"}
        };

        for (String[] item : allMenuItems) {
            String menuName = item[1];

            // Skip "Manage Staff" entirely for Staff role
            if ("Manage Staff".equals(menuName) && !isAdmin) {
                continue;
            }

            JButton btn = createSidebarButton(item[0], menuName);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(230, 45));
            menuButtons.put(menuName, btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        /* ── USER INFO at the bottom of the sidebar ─────────────── */
        JPanel userPanel = new JPanel(new BorderLayout(10, 0));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(230, 60));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel userIcon = new JLabel(isAdmin ? "👑" : "👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);

        // Show the real full name from the DB
        String displayName = fullName.isEmpty()
                ? mainSystem.getLoggedInUsername()
                : fullName;
        JLabel userName = new JLabel(displayName);
        userName.setForeground(Color.WHITE);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel userRoleLabel = new JLabel(isAdmin ? "Administrator" : "Staff Member");
        userRoleLabel.setForeground(isAdmin
                ? new Color(144, 202, 249)
                : new Color(165, 214, 167));
        userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        userInfo.add(userName);
        userInfo.add(userRoleLabel);

        userPanel.add(userIcon,      BorderLayout.WEST);
        userPanel.add(userInfo,      BorderLayout.CENTER);
        sidebar.add(userPanel);

        /* ── CONTENT AREA ──────────────────────────────────────── */
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_COLOR);

        main.add(sidebar,     BorderLayout.WEST);
        main.add(contentArea, BorderLayout.CENTER);

        setContentPane(main);
        navigateTo("Dashboard");
        setVisible(true);
    }

    private JButton createSidebarButton(String icon, String text) {
        JButton btn = new JButton("  " + icon + "   " + text);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.setForeground(new Color(255, 255, 255, 200));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!text.equals(currentSection)) btn.setBackground(SIDEBAR_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!text.equals(currentSection)) btn.setBackground(SIDEBAR_COLOR);
            }
        });

        btn.addActionListener(e -> navigateTo(text));
        return btn;
    }

    private void navigateTo(String section) {
        // Reset previous button highlight
        if (menuButtons.containsKey(currentSection)) {
            menuButtons.get(currentSection).setBackground(SIDEBAR_COLOR);
            menuButtons.get(currentSection).setForeground(new Color(255, 255, 255, 200));
        }

        currentSection = section;

        // Highlight active button
        if (menuButtons.containsKey(section)) {
            menuButtons.get(section).setBackground(PRIMARY_COLOR);
            menuButtons.get(section).setForeground(Color.WHITE);
        }

        contentArea.removeAll();

        switch (section) {
            case "Dashboard":
                new Dashboard(mainSystem, contentArea);
                break;
            case "Add Reservation":
                new AddReservation(mainSystem, contentArea);
                break;
            case "View Reservations":
                new ViewReservations(mainSystem, contentArea);
                break;
            case "Billing":
                new Billing(mainSystem, contentArea);
                break;
            case "Analytics":
                new Analytics(mainSystem, contentArea);
                break;
            case "Manage Staff":
                // Double-check role even though button is hidden for Staff
                if ("Admin".equalsIgnoreCase(mainSystem.getLoggedInRole())) {
                    new ManageStaff(mainSystem, contentArea);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Access Denied.\nOnly Admins can manage staff accounts.",
                        "Permission Denied", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case "Help":
                new Help(mainSystem, contentArea);
                break;
            case "Logout":
                int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new OceanResortSystem();   // go back to login
                }
                return;
        }

        contentArea.revalidate();
        contentArea.repaint();
    }
}
