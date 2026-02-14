package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Analytics {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public Analytics(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showAnalytics();
    }

    private void showAnalytics() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
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
        
        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        for (OceanResortSystem.Reservation r : reservations.values()) {
            long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
            int rate = mainSystem.getRoomRate(r.roomType);
            revenueByRoom.put(r.roomType, revenueByRoom.get(r.roomType) + (nights * rate));
        }

        statsPanel.add(createAnalyticsCard("Single Room Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Single")), mainSystem.INFO_COLOR));
        statsPanel.add(createAnalyticsCard("Double Room Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Double")), mainSystem.SUCCESS_COLOR));
        statsPanel.add(createAnalyticsCard("Suite Revenue", 
            "LKR " + String.format("%,d", revenueByRoom.get("Suite")), new Color(156, 39, 176)));
        statsPanel.add(createAnalyticsCard("Average Booking Duration", 
            calculateAverageDuration() + " nights", mainSystem.WARNING_COLOR));

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
        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        if (reservations.isEmpty()) return "0";
        long totalNights = 0;
        for (OceanResortSystem.Reservation r : reservations.values()) {
            totalNights += ChronoUnit.DAYS.between(r.checkIn, r.checkOut);
        }
        return String.format("%.1f", (double) totalNights / reservations.size());
    }
}
