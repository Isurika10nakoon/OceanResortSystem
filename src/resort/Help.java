package resort;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Help {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public Help(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showHelp();
    }

    private void showHelp() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
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
}
