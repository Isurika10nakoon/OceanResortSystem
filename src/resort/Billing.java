package resort;

import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Billing {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public Billing(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showBilling();
    }

    private void showBilling() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
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
        
        ModernButton genBtn = new ModernButton("GENERATE INVOICE", mainSystem.PRIMARY_COLOR);
        genBtn.setPreferredSize(new Dimension(190, 50));

        searchPanel.add(billInput, BorderLayout.CENTER);
        searchPanel.add(genBtn, BorderLayout.EAST);

        JEditorPane invoicePane = new JEditorPane("text/html", "");
        invoicePane.setEditable(false);
        invoicePane.setBackground(Color.WHITE);
        invoicePane.setText(getEmptyInvoiceHTML());

        genBtn.addActionListener(e -> {
            OceanResortSystem.Reservation r = mainSystem.getReservations().get(billInput.getText());
            if (r != null) {
                invoicePane.setText(generateModernInvoiceHTML(r));
            } else {
                JOptionPane.showMessageDialog(null, "Reservation not found!", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private String generateModernInvoiceHTML(OceanResortSystem.Reservation r) {
        long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
        int rate = mainSystem.getRoomRate(r.roomType);
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
}
