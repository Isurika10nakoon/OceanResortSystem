package resort;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.SpinnerDateModel;

public class OceanResortSystem extends JFrame {

    /* ===================== DATA MODEL ===================== */
    static class Reservation {
        String resNo, name, address, contact, roomType;
        LocalDate checkIn, checkOut;

        Reservation(String resNo, String name, String address, String contact,
                    String roomType, LocalDate checkIn, LocalDate checkOut) {
            this.resNo = resNo;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.roomType = roomType;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
        }
    }

    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static final String FILE_NAME = "reservations.txt";

    /* ===================== MODERN THEME ===================== */
    private final Color PRIMARY_COLOR = new Color(28, 78, 128);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color SIDEBAR_COLOR = new Color(33, 37, 41);
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font SUB_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private JPanel contentArea;

    public OceanResortSystem() {
        loadReservationsFromFile(); // 💾 load saved data
        showLoginUI();
    }

    /* ===================== CUSTOM MODERN COMPONENTS ===================== */
    class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) g2.setColor(PRIMARY_COLOR.darker());
            else if (getModel().isRollover()) g2.setColor(ACCENT_COLOR);
            else g2.setColor(PRIMARY_COLOR);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class ModernTextField extends JTextField {
        public ModernTextField() {
            setOpaque(false);
            setFont(MAIN_FONT);
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(new Color(225, 225, 225));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class RoundedBorder extends EmptyBorder {
        private int radius;

        RoundedBorder(int radius) {
            super(0, 0, 0, 0);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(230, 230, 230));
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
        }
    }

    /* ===================== AUTH & NAVIGATION ===================== */
    private void showLoginUI() {
        setTitle("Ocean Resort | Staff Login");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_COLOR);
        p.setBorder(new EmptyBorder(60, 50, 60, 50));
        JLabel logo = new JLabel("OCEAN RESORT", JLabel.CENTER);
        logo.setFont(TITLE_FONT);
        logo.setForeground(PRIMARY_COLOR);
        JPanel f = new JPanel(new GridLayout(4, 1, 10, 10));
        f.setOpaque(false);
        ModernTextField u = new ModernTextField();
        JPasswordField ps = new JPasswordField();
        ps.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(12),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        f.add(new JLabel("Username"));
        f.add(u);
        f.add(new JLabel("Password"));
        f.add(ps);
        ModernButton b = new ModernButton("LOGIN");
        b.setPreferredSize(new Dimension(0, 50));
        b.addActionListener(e -> {
            String username = u.getText().trim();
            String password = new String(ps.getPassword());
            if (username.equals("admin") && password.equals("admin123")) {
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                u.setText("");
                ps.setText("");
                u.requestFocus();
            }
        });

        p.add(logo, BorderLayout.NORTH);
        p.add(f, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);
        setContentPane(p);
        setVisible(true);
    }

    private void showMainMenu() {
        setTitle("Ocean Resort Management System");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        JPanel main = new JPanel(new BorderLayout());
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(40, 25, 40, 25));
        JLabel sideHeader = new JLabel("OCEAN RESORT");
        sideHeader.setForeground(Color.WHITE);
        sideHeader.setFont(SUB_FONT);
        sideHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(sideHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));
        String[] menuItems = {"Dashboard", "Add Reservation", "Display Details", "Print Billing", "Help Section", "Logout"};
        for (String item : menuItems) {
            ModernButton btn = new ModernButton(item);
            btn.setMaximumSize(new Dimension(220, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.addActionListener(e -> navigateTo(item));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_COLOR);
        main.add(sidebar, BorderLayout.WEST);
        main.add(contentArea, BorderLayout.CENTER);
        setContentPane(main);
        navigateTo("Dashboard");
        revalidate();
    }

    private void navigateTo(String section) {
        contentArea.removeAll();
        switch (section) {
            case "Dashboard" -> showDashboard();
            case "Add Reservation" -> showAddReservation();
            case "Display Details" -> showDisplayDetails();
            case "Print Billing" -> showBilling();
            case "Help Section" -> showHelp();
            case "Logout" -> System.exit(0);
        }
        contentArea.revalidate();
        contentArea.repaint();
    }

    /* ===================== DASHBOARD MODULE ===================== */
    private void showDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header
        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(TITLE_FONT);
        header.setForeground(PRIMARY_COLOR);

        // Stats Cards Container
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Calculate statistics
        int totalReservations = reservations.size();
        int activeReservations = 0;
        long totalRevenue = 0;

        LocalDate today = LocalDate.now();
        Map<String, Integer> roomTypeCount = new HashMap<>();
        roomTypeCount.put("Single", 0);
        roomTypeCount.put("Double", 0);
        roomTypeCount.put("Suite", 0);

        for (Reservation r : reservations.values()) {
            // Count active reservations
            if (!r.checkIn.isAfter(today) && !r.checkOut.isBefore(today)) {
                activeReservations++;
            }

            // Calculate revenue
            long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
            int rate = r.roomType.equals("Single") ? 8000 : r.roomType.equals("Double") ? 12000 : 20000;
            totalRevenue += nights * rate;

            // Count room types
            roomTypeCount.put(r.roomType, roomTypeCount.get(r.roomType) + 1);
        }

        // Create stat cards
        statsGrid.add(createStatCard("Total Reservations", String.valueOf(totalReservations), new Color(52, 152, 219)));
        statsGrid.add(createStatCard("Active Guests", String.valueOf(activeReservations), new Color(46, 204, 113)));
        statsGrid.add(createStatCard("Total Revenue", "LKR " + String.format("%,d", totalRevenue), new Color(155, 89, 182)));

        // Bottom Section - Recent Activity & Room Distribution
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSection.setOpaque(false);

        // Recent Reservations Panel
        JPanel recentPanel = createDashboardCard("Recent Reservations");
        JPanel recentList = new JPanel();
        recentList.setLayout(new BoxLayout(recentList, BoxLayout.Y_AXIS));
        recentList.setOpaque(false);

        ArrayList<Reservation> sortedRes = new ArrayList<>(reservations.values());
        sortedRes.sort((a, b) -> b.checkIn.compareTo(a.checkIn));

        int count = 0;
        for (Reservation r : sortedRes) {
            if (count >= 5) break;
            JPanel resItem = new JPanel(new BorderLayout(10, 0));
            resItem.setOpaque(false);
            resItem.setBorder(new EmptyBorder(10, 0, 10, 0));

            JLabel nameLabel = new JLabel(r.name);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            JLabel dateLabel = new JLabel(r.checkIn.toString());
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);

            resItem.add(nameLabel, BorderLayout.WEST);
            resItem.add(dateLabel, BorderLayout.EAST);
            recentList.add(resItem);
            
            if (count < 4 && count < sortedRes.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                recentList.add(sep);
            }
            count++;
        }

        if (reservations.isEmpty()) {
            JLabel emptyLabel = new JLabel("No reservations yet", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.LIGHT_GRAY);
            recentList.add(emptyLabel);
        }

        JScrollPane recentScroll = new JScrollPane(recentList);
        recentScroll.setBorder(null);
        recentScroll.setOpaque(false);
        recentScroll.getViewport().setOpaque(false);
        recentPanel.add(recentScroll, BorderLayout.CENTER);

        // Room Distribution Panel
        JPanel roomPanel = createDashboardCard("Room Type Distribution");
        JPanel roomStats = new JPanel(new GridLayout(3, 1, 0, 15));
        roomStats.setOpaque(false);
        roomStats.setBorder(new EmptyBorder(20, 0, 0, 0));

        roomStats.add(createRoomBar("Single Room", roomTypeCount.get("Single"), totalReservations, new Color(52, 152, 219)));
        roomStats.add(createRoomBar("Double Room", roomTypeCount.get("Double"), totalReservations, new Color(46, 204, 113)));
        roomStats.add(createRoomBar("Suite", roomTypeCount.get("Suite"), totalReservations, new Color(155, 89, 182)));

        roomPanel.add(roomStats, BorderLayout.CENTER);

        bottomSection.add(recentPanel);
        bottomSection.add(roomPanel);

        // Assemble dashboard
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(statsGrid, BorderLayout.NORTH);
        content.add(bottomSection, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(0, 4));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(colorBar, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createDashboardCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);

        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    private JPanel createRoomBar(String roomType, int count, int total, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(roomType);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel countLabel = new JLabel(count + " / " + total);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setForeground(color);

        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setOpaque(false);
        barContainer.setPreferredSize(new Dimension(0, 8));
        barContainer.setBackground(new Color(240, 240, 240));

        JPanel barFill = new JPanel();
        barFill.setBackground(color);
        int percentage = total > 0 ? (int) ((count * 100.0) / total) : 0;
        barFill.setPreferredSize(new Dimension((int) (barContainer.getPreferredSize().width * percentage / 100.0), 8));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(label, BorderLayout.WEST);
        topRow.add(countLabel, BorderLayout.EAST);

        panel.add(topRow, BorderLayout.NORTH);
        
        JPanel barWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 240, 240));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, 8, 8, 8));
                
                if (total > 0) {
                    int barWidth = (int) ((getWidth() - 1) * percentage / 100.0);
                    g2.setColor(color);
                    g2.fill(new RoundRectangle2D.Double(0, 0, barWidth, 8, 8, 8));
                }
                g2.dispose();
            }
        };
        barWrapper.setOpaque(false);
        barWrapper.setPreferredSize(new Dimension(0, 8));
        
        panel.add(barWrapper, BorderLayout.CENTER);

        return panel;
    }

    /* ===================== CORE MODULES ===================== */
    private void showAddReservation() {
        JPanel p = createContentPanel("New Guest Registration");
        JPanel grid = new JPanel(new GridLayout(7, 2, 20, 15));
        grid.setOpaque(false);
        ModernTextField resNo = new ModernTextField();
        ModernTextField name = new ModernTextField();
        ModernTextField addr = new ModernTextField();
        ModernTextField cont = new ModernTextField();
        JComboBox<String> room = new JComboBox<>(new String[]{"Single", "Double", "Suite"});

        // ---- DATE PICKERS ----
        SpinnerDateModel checkInModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkInSpinner = new JSpinner(checkInModel);
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));

        SpinnerDateModel checkOutModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkOutSpinner = new JSpinner(checkOutModel);
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));

        grid.add(new JLabel("Reservation No:"));
        grid.add(resNo);
        grid.add(new JLabel("Full Name:"));
        grid.add(name);
        grid.add(new JLabel("Address:"));
        grid.add(addr);
        grid.add(new JLabel("Contact:"));
        grid.add(cont);
        grid.add(new JLabel("Room Class:"));
        grid.add(room);
        grid.add(new JLabel("Check-In:"));
        grid.add(checkInSpinner);
        grid.add(new JLabel("Check-Out:"));
        grid.add(checkOutSpinner);

        ModernButton save = new ModernButton("SAVE RESERVATION");
        save.setPreferredSize(new Dimension(0, 50));
        save.addActionListener(e -> {
            try {
                Date inDate = (Date) checkInSpinner.getValue();
                Date outDate = (Date) checkOutSpinner.getValue();

                LocalDate checkIn = inDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate checkOut = outDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();

                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Check-out date must be after Check-in date!",
                            "Date Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                reservations.put(
                        resNo.getText(),
                        new Reservation(
                                resNo.getText(),
                                name.getText(),
                                addr.getText(),
                                cont.getText(),
                                room.getSelectedItem().toString(),
                                checkIn,
                                checkOut
                        )
                );

                saveReservationsToFile(); // 💾 persist data
                JOptionPane.showMessageDialog(this, "Reservation saved successfully!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid reservation details!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        p.add(grid, BorderLayout.CENTER);
        p.add(save, BorderLayout.SOUTH);
        contentArea.add(p);
    }

    private void showDisplayDetails() {
        JPanel panel = createContentPanel("Guest Information Center");
        JPanel searchBar = new JPanel(new BorderLayout(15, 0));
        searchBar.setOpaque(false);
        ModernTextField searchInput = new ModernTextField();
        ModernButton searchBtn = new ModernButton("SEARCH RECORDS");
        searchBtn.setPreferredSize(new Dimension(160, 45));
        searchBar.add(new JLabel("Reservation ID: "), BorderLayout.WEST);
        searchBar.add(searchInput, BorderLayout.CENTER);
        searchBar.add(searchBtn, BorderLayout.EAST);

        JPanel displayWrapper = new JPanel(new CardLayout());
        displayWrapper.setOpaque(false);
        displayWrapper.setBorder(new EmptyBorder(30, 0, 0, 0));
        JLabel placeholder = new JLabel("Enter a Guest ID above to view details", JLabel.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        placeholder.setForeground(Color.LIGHT_GRAY);

        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(20), new EmptyBorder(30, 30, 30, 30)));

        searchBtn.addActionListener(e -> {
            Reservation r = reservations.get(searchInput.getText());
            if (r != null) {
                infoCard.removeAll();
                JLabel nameLabel = new JLabel(r.name.toUpperCase());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
                nameLabel.setForeground(PRIMARY_COLOR);

                JPanel detailsGrid = new JPanel(new GridLayout(3, 2, 20, 20));
                detailsGrid.setOpaque(false);
                detailsGrid.setBorder(new EmptyBorder(20, 0, 0, 0));
                detailsGrid.add(createDataPair("ROOM TYPE", r.roomType));
                detailsGrid.add(createDataPair("CONTACT", r.contact));
                detailsGrid.add(createDataPair("CHECK-IN", r.checkIn.toString()));
                detailsGrid.add(createDataPair("CHECK-OUT", r.checkOut.toString()));
                detailsGrid.add(createDataPair("ADDRESS", r.address));
                detailsGrid.add(createDataPair("STAY DURATION", ChronoUnit.DAYS.between(r.checkIn, r.checkOut) + " Night(s)"));

                infoCard.add(nameLabel, BorderLayout.NORTH);
                infoCard.add(detailsGrid, BorderLayout.CENTER);
                displayWrapper.add(infoCard, "INFO");
                ((CardLayout) displayWrapper.getLayout()).show(displayWrapper, "INFO");
            } else {
                JOptionPane.showMessageDialog(this, "No record found.");
            }
        });

        displayWrapper.add(placeholder, "EMPTY");
        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(displayWrapper, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    private void showBilling() {
        JPanel panel = createContentPanel("Billing & Invoice Management");

        JPanel topSearch = new JPanel(new BorderLayout(15, 0));
        topSearch.setOpaque(false);
        ModernTextField billInput = new ModernTextField();
        billInput.setToolTipText("Enter Guest Reservation ID...");
        ModernButton genBtn = new ModernButton("GENERATE INVOICE");
        genBtn.setPreferredSize(new Dimension(180, 45));

        topSearch.add(new JLabel("Reservation ID: "), BorderLayout.WEST);
        topSearch.add(billInput, BorderLayout.CENTER);
        topSearch.add(genBtn, BorderLayout.EAST);

        JPanel invoiceWrapper = new JPanel(new BorderLayout());
        invoiceWrapper.setOpaque(false);
        invoiceWrapper.setBorder(new EmptyBorder(30, 0, 0, 0));

        JEditorPane invoicePane = new JEditorPane("text/html", "");
        invoicePane.setEditable(false);
        invoicePane.setBackground(BG_COLOR);
        invoicePane.setText("<html><body style='text-align:center; padding-top:50px; font-family:Segoe UI; color:#aaa;'>"
                + "<h3>No invoice generated.</h3><p>Enter a valid ID and click generate.</p></body></html>");

        genBtn.addActionListener(e -> {
            Reservation r = reservations.get(billInput.getText());
            if (r != null) {
                long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
                int rate = r.roomType.equals("Single") ? 8000 : r.roomType.equals("Double") ? 12000 : 20000;
                long total = nights * rate;

                String htmlInvoice = "<html><body style='font-family:Segoe UI, sans-serif; padding:25px; color:#333;'>"
                        + "<div style='background-color:#fff; border:1px solid #eee; padding:30px; border-radius:15px;'>"
                        + "<table width='100%'><tr>"
                        + "<td><h1 style='color:#1C4E80; margin:0;'>INVOICE</h1><p style='color:#777;'>#INV-" + r.resNo + "</p></td>"
                        + "<td align='right'><h2 style='margin:0;'>OCEAN RESORT</h2><p style='color:#777;'>Date: 2026-01-25</p></td>"
                        + "</tr></table>"
                        + "<hr style='border:0; border-top:1px solid #eee; margin:20px 0;'>"
                        + "<table width='100%'><tr>"
                        + "<td><b>BILLED TO:</b><br>" + r.name + "<br>" + r.address + "</td>"
                        + "<td align='right'><b>RESERVATION DETAILS:</b><br>Room: " + r.roomType + "<br>Stay: " + nights + " Night(s)</td>"
                        + "</tr></table>"
                        + "<table width='100%' style='margin-top:30px; border-collapse:collapse;'>"
                        + "<tr style='background-color:#1C4E80; color:#fff;'>"
                        + "<th style='padding:12px; text-align:left;'>Description</th>"
                        + "<th style='padding:12px; text-align:right;'>Rate</th>"
                        + "<th style='padding:12px; text-align:right;'>Amount</th>"
                        + "</tr>"
                        + "<tr>"
                        + "<td style='padding:12px; border-bottom:1px solid #eee;'>Accommodation (Room Class: " + r.roomType + ")</td>"
                        + "<td style='padding:12px; border-bottom:1px solid #eee; text-align:right;'>LKR " + rate + "</td>"
                        + "<td style='padding:12px; border-bottom:1px solid #eee; text-align:right;'>LKR " + total + "</td>"
                        + "</tr></table>"
                        + "<div style='margin-top:40px; text-align:right;'>"
                        + "<h4 style='margin:0; color:#777;'>GRAND TOTAL</h4>"
                        + "<h1 style='margin:0; color:#1C4E80;'>LKR " + String.format("%,d", total) + "</h1>"
                        + "</div>"
                        + "<p style='margin-top:50px; font-size:10px; color:#aaa; text-align:center;'>Thank you for choosing Ocean Resort. Please keep this for your records.</p>"
                        + "</div></body></html>";

                invoicePane.setText(htmlInvoice);
            } else {
                JOptionPane.showMessageDialog(this, "Record not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JScrollPane scroll = new JScrollPane(invoicePane);
        scroll.setBorder(new RoundedBorder(15));
        invoiceWrapper.add(scroll, BorderLayout.CENTER);

        panel.add(topSearch, BorderLayout.NORTH);
        panel.add(invoiceWrapper, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    private void showHelp() {
        JPanel panel = createContentPanel("Staff Operational Manual");
        JEditorPane helpPane = new JEditorPane("text/html", "");
        helpPane.setEditable(false);

        String helpContent = "<html><body style='font-family:Segoe UI, sans-serif; padding:15px; color:#333;'>"
                + "<h2 style='color:#1C4E80; border-bottom: 2px solid #1C4E80;'>1. Room Rates & Categories</h2>"
                + "<table style='width:100%; border-collapse: collapse; margin-bottom: 20px;'>"
                + "<tr style='background-color:#f2f2f2;'>"
                + "<th style='padding:10px; border:1px solid #ddd; text-align:left;'>Room Type</th>"
                + "<th style='padding:10px; border:1px solid #ddd; text-align:left;'>Daily Rate (LKR)</th>"
                + "<th style='padding:10px; border:1px solid #ddd; text-align:left;'>Inclusions</th>"
                + "</tr>"
                + "<tr><td style='padding:8px; border:1px solid #ddd;'>Single</td><td style='padding:8px; border:1px solid #ddd;'><b>8,000</b></td><td style='padding:8px; border:1px solid #ddd;'>1 Adult, Breakfast</td></tr>"
                + "<tr><td style='padding:8px; border:1px solid #ddd;'>Double</td><td style='padding:8px; border:1px solid #ddd;'><b>12,000</b></td><td style='padding:8px; border:1px solid #ddd;'>2 Adults, Breakfast</td></tr>"
                + "<tr><td style='padding:8px; border:1px solid #ddd;'>Suite</td><td style='padding:8px; border:1px solid #ddd;'><b>20,000</b></td><td style='padding:8px; border:1px solid #ddd;'>4 Adults, Full Board</td></tr>"
                + "</table>"
                + "<h2 style='color:#1C4E80; border-bottom: 2px solid #1C4E80;'>2. Check-In Protocol</h2>"
                + "<ol><li>Collect Guest ID/Passport.</li><li>Enter Reservation No.</li><li>Use <b>YYYY-MM-DD</b> format.</li></ol>"
                + "<div style='background-color:#E8F4F8; padding:15px; border-radius:10px; margin-top:20px;'>"
                + "<b>IT Support:</b> Ext 404 | <b>Manager:</b> Ext 101</div></body></html>";

        helpPane.setText(helpContent);
        JScrollPane scroll = new JScrollPane(helpPane);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    /* ===================== FILE HANDLING ===================== */
    private void saveReservationsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Reservation r : reservations.values()) {
                bw.write(r.resNo + "," + r.name + "," + r.address + "," + r.contact + "," + r.roomType + "," +
                        r.checkIn + "," + r.checkOut);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving reservations!");
        }
    }

    private void loadReservationsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Reservation r = new Reservation(
                        data[0], data[1], data[2], data[3], data[4],
                        LocalDate.parse(data[5]),
                        LocalDate.parse(data[6])
                );
                reservations.put(r.resNo, r);
            }
        } catch (IOException e) {
            // Ignore first-time run (file may not exist)
        }
    }

    /* ===================== UI HELPERS ===================== */
    private JPanel createContentPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(40, 50, 40, 50));
        JLabel l = new JLabel(title);
        l.setFont(TITLE_FONT);
        l.setForeground(PRIMARY_COLOR);
        p.add(l, BorderLayout.NORTH);
        return p;
    }

    private JPanel createDataPair(String label, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Color.GRAY);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        v.setForeground(Color.BLACK);
        p.add(l);
        p.add(v);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OceanResortSystem::new);
    }
}
