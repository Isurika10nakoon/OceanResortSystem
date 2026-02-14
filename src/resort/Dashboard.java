package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Dashboard {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public Dashboard(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showDashboard();
    }

    private void showDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
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

        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        int totalReservations = reservations.size();
        int activeReservations = 0;
        int upcomingReservations = 0;
        long totalRevenue = 0;

        LocalDate today = LocalDate.now();
        for (OceanResortSystem.Reservation r : reservations.values()) {
            r.updateStatus();
            if (r.status.equals("Active")) activeReservations++;
            if (r.status.equals("Upcoming")) upcomingReservations++;

            long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
            int rate = mainSystem.getRoomRate(r.roomType);
            totalRevenue += nights * rate;
        }

        statsGrid.add(createModernStatCard("Total Bookings", String.valueOf(totalReservations), "📊", mainSystem.INFO_COLOR));
        statsGrid.add(createModernStatCard("Active Guests", String.valueOf(activeReservations), "✅", mainSystem.SUCCESS_COLOR));
        statsGrid.add(createModernStatCard("Upcoming", String.valueOf(upcomingReservations), "📅", mainSystem.WARNING_COLOR));
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

        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        ArrayList<OceanResortSystem.Reservation> sortedRes = new ArrayList<>(reservations.values());
        sortedRes.sort((a, b) -> b.checkIn.compareTo(a.checkIn));

        int count = 0;
        for (OceanResortSystem.Reservation r : sortedRes) {
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
                statusColor = mainSystem.SUCCESS_COLOR;
            } else if (r.status.equals("Upcoming")) {
                statusColor = mainSystem.WARNING_COLOR;
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
        
        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        for (OceanResortSystem.Reservation r : reservations.values()) {
            roomTypeCount.put(r.roomType, roomTypeCount.get(r.roomType) + 1);
        }
        
        int total = reservations.size();
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        statsPanel.add(createProgressBar("Single Room", roomTypeCount.get("Single"), total, mainSystem.INFO_COLOR));
        statsPanel.add(createProgressBar("Double Room", roomTypeCount.get("Double"), total, mainSystem.SUCCESS_COLOR));
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
}
