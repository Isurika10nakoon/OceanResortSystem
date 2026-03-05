package resort;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class Billing {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    // Keep reference to current reservation for printing
    private OceanResortSystem.Reservation currentReservation = null;

    public Billing(OceanResortSystem system, JPanel content) {
        this.mainSystem  = system;
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

        // ── Search + button row ──────────────────────────────────
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        ModernTextField billInput = new ModernTextField();
        billInput.setPlaceholder("Enter Reservation ID (e.g., RES-001)");
        billInput.setPreferredSize(new Dimension(0, 50));

        // Button panel — Generate + Print side by side
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        ModernButton genBtn   = new ModernButton("GENERATE INVOICE", mainSystem.PRIMARY_COLOR);
        genBtn.setPreferredSize(new Dimension(200, 50));

        ModernButton printBtn = new ModernButton("🖨  PRINT INVOICE", new Color(117, 117, 117));
        printBtn.setPreferredSize(new Dimension(175, 50));
        printBtn.setEnabled(false);   // disabled until invoice is generated

        btnPanel.add(genBtn);
        btnPanel.add(printBtn);

        searchPanel.add(billInput, BorderLayout.CENTER);
        searchPanel.add(btnPanel,  BorderLayout.EAST);

        // ── Invoice display pane ─────────────────────────────────
        JEditorPane invoicePane = new JEditorPane("text/html", "");
        invoicePane.setEditable(false);
        invoicePane.setBackground(Color.WHITE);
        invoicePane.setText(getEmptyInvoiceHTML());

        // ── GENERATE action ──────────────────────────────────────
        genBtn.addActionListener(e -> {
            String id = billInput.getText().trim();
            mainSystem.loadReservationsFromDB();
            OceanResortSystem.Reservation r = mainSystem.getReservations().get(id);

            if (r != null) {
                currentReservation = r;
                invoicePane.setText(generateModernInvoiceHTML(r));
                // Scroll back to top
                invoicePane.setCaretPosition(0);
                // Enable print button and turn it blue
                printBtn.setEnabled(true);
                setPrintButtonActive(printBtn, true);
            } else {
                currentReservation = null;
                printBtn.setEnabled(false);
                setPrintButtonActive(printBtn, false);
                JOptionPane.showMessageDialog(null,
                    "Reservation ID \"" + id + "\" not found in database!",
                    "Not Found", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ── PRINT action ─────────────────────────────────────────
        printBtn.addActionListener(e -> {
            if (currentReservation == null) return;
            printInvoice(currentReservation);
        });

        // Also allow pressing Enter in the search field
        billInput.addActionListener(e -> genBtn.doClick());

        JScrollPane scroll = new JScrollPane(invoicePane);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JPanel invoiceContainer = new JPanel(new BorderLayout());
        invoiceContainer.setOpaque(false);
        invoiceContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        invoiceContainer.add(scroll);

        panel.add(header, BorderLayout.NORTH);
        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setOpaque(false);
        topPanel.add(searchPanel,      BorderLayout.NORTH);
        topPanel.add(invoiceContainer, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.CENTER);
        contentArea.add(panel);
    }

    /* ══════════════════════════════════════════════════════════
       PRINT INVOICE
       Uses Java2D printing — opens the system print dialog,
       then renders a clean invoice onto the page.
    ══════════════════════════════════════════════════════════ */
    private void printInvoice(OceanResortSystem.Reservation r) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Invoice — " + r.resNo);

        // Page format: A4-ish portrait
        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Translate to printable area
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double pw = pageFormat.getImageableWidth();
            double ph = pageFormat.getImageableHeight();

            // ── Draw invoice content ─────────────────────────────
            drawPrintableInvoice(g2, r, (int) pw, (int) ph);

            return Printable.PAGE_EXISTS;
        }, pf);

        // Show print dialog
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null,
                    "Printing failed:\n" + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /* ══════════════════════════════════════════════════════════
       DRAW INVOICE ON PAPER (Java2D)
    ══════════════════════════════════════════════════════════ */
    private void drawPrintableInvoice(Graphics2D g, OceanResortSystem.Reservation r,
                                      int pageW, int pageH) {

        long   nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
        int    rate   = mainSystem.getRoomRate(r.roomType);
        long   total  = nights * rate;

        Color  blue      = new Color(25, 118, 210);
        Color  lightBlue = new Color(227, 242, 253);
        Color  gray      = new Color(117, 117, 117);
        Color  lightGray = new Color(245, 245, 245);
        Color  border    = new Color(224, 224, 224);

        int y = 0;
        int pad = 25;
        int colW = pageW - pad * 2;

        // ── Header banner ────────────────────────────────────────
        g.setColor(blue);
        g.fillRoundRect(pad, y + 10, colW, 75, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("OCEAN RESORT", pad + 20, y + 45);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString("Luxury Beachfront Experience", pad + 20, y + 62);

        // Invoice label on right
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("INVOICE", pageW - pad - 90, y + 45);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.drawString("#" + r.resNo, pageW - pad - 90, y + 62);

        y += 100;

        // ── Invoice meta row ─────────────────────────────────────
        g.setColor(lightGray);
        g.fillRoundRect(pad, y, colW, 50, 8, 8);

        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.setColor(gray);
        g.drawString("INVOICE DATE", pad + 15, y + 18);
        g.drawString("STATUS", pad + 160, y + 18);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(Color.BLACK);
        g.drawString(LocalDate.now().toString(), pad + 15, y + 35);
        g.setColor(new Color(76, 175, 80));
        g.drawString("● PAID", pad + 160, y + 35);

        y += 65;

        // ── Billed To / Stay Details ─────────────────────────────
        int halfW = (colW - 15) / 2;

        // Left card — Billed To
        g.setColor(lightGray);
        g.fillRoundRect(pad, y, halfW, 95, 8, 8);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.setColor(gray);
        g.drawString("BILLED TO", pad + 12, y + 18);
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        g.drawString(r.name, pad + 12, y + 35);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(gray);
        g.drawString(r.address, pad + 12, y + 52);
        g.drawString(r.contact, pad + 12, y + 67);

        // Right card — Stay Details
        int rx = pad + halfW + 15;
        g.setColor(lightGray);
        g.fillRoundRect(rx, y, halfW, 95, 8, 8);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.setColor(gray);
        g.drawString("STAY DETAILS", rx + 12, y + 18);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g.setColor(Color.BLACK);
        g.drawString("Room:       " + r.roomType,      rx + 12, y + 35);
        g.drawString("Check-In:   " + r.checkIn,       rx + 12, y + 50);
        g.drawString("Check-Out:  " + r.checkOut,      rx + 12, y + 65);
        g.drawString("Duration:   " + nights + " Night(s)", rx + 12, y + 80);

        y += 110;

        // ── Line items table ─────────────────────────────────────
        // Table header
        g.setColor(blue);
        g.fillRoundRect(pad, y, colW, 32, 6, 6);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g.drawString("Description",           pad + 12,      y + 21);
        g.drawString("Nights",                pad + 270,     y + 21);
        g.drawString("Rate / Night",          pad + 360,     y + 21);
        g.drawString("Amount",                pageW - pad - 70, y + 21);
        y += 32;

        // Row
        g.setColor(new Color(250, 250, 250));
        g.fillRect(pad, y, colW, 40);
        g.setColor(border);
        g.drawLine(pad, y + 40, pad + colW, y + 40);

        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString("Accommodation — " + r.roomType + " Room", pad + 12, y + 18);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g.setColor(gray);
        g.drawString("Res. No: " + r.resNo, pad + 12, y + 33);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(nights),                   pad + 278,     y + 25);
        g.drawString("LKR " + String.format("%,d", rate),     pad + 360,     y + 25);
        g.drawString("LKR " + String.format("%,d", total),    pageW - pad - 100, y + 25);

        y += 55;

        // ── Total box ────────────────────────────────────────────
        g.setColor(lightBlue);
        g.fillRoundRect(pad + halfW + 15, y, halfW, 60, 8, 8);
        g.setColor(gray);
        g.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g.drawString("TOTAL AMOUNT DUE", rx + 12, y + 18);
        g.setColor(blue);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("LKR " + String.format("%,d", total), rx + 12, y + 45);

        y += 80;

        // ── Footer ───────────────────────────────────────────────
        g.setColor(border);
        g.drawLine(pad, y, pad + colW, y);
        y += 15;

        g.setColor(gray);
        g.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        g.drawString("Thank you for choosing Ocean Resort! For inquiries: +94 11 234 5678 | info@oceanresort.com", pad, y + 12);
    }

    /* ══════════════════════════════════════════════════════════
       HELPER: toggle print button color
    ══════════════════════════════════════════════════════════ */
    private void setPrintButtonActive(ModernButton btn, boolean active) {
        // We can't change baseColor directly (private), so we swap the button
        // Instead, rely on enabled state + visual cue via text
        if (active) {
            btn.setForeground(Color.WHITE);
        } else {
            btn.setForeground(new Color(180, 180, 180));
        }
    }

    /* ══════════════════════════════════════════════════════════
       EMPTY STATE HTML
    ══════════════════════════════════════════════════════════ */
    private String getEmptyInvoiceHTML() {
        return "<html><body style='text-align:center; padding:150px 50px;"
             + " font-family:Segoe UI; color:#aaa; background:#fafafa;'>"
             + "<h1 style='color:#ccc; font-size:48px;'>📄</h1>"
             + "<h2>No Invoice Generated</h2>"
             + "<p>Enter a Reservation ID and click 'Generate Invoice'</p>"
             + "</body></html>";
    }

    /* ══════════════════════════════════════════════════════════
       INVOICE HTML (on-screen display)
    ══════════════════════════════════════════════════════════ */
    private String generateModernInvoiceHTML(OceanResortSystem.Reservation r) {
        long nights = Math.max(1, ChronoUnit.DAYS.between(r.checkIn, r.checkOut));
        int  rate   = mainSystem.getRoomRate(r.roomType);
        long total  = nights * rate;

        return "<html><body style='font-family:Segoe UI; margin:0; padding:50px; background:#f5f5f5;'>"
             + "<div style='max-width:800px; margin:0 auto; background:white; padding:50px;"
             +      " border-radius:12px; box-shadow:0 4px 20px rgba(0,0,0,0.1);'>"
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
             + "<td style='vertical-align:top;'>"
             +   "<p style='color:#999; margin:0; font-size:12px;'>BILLED TO</p>"
             +   "<h3 style='margin:10px 0; color:#333;'>" + r.name + "</h3>"
             +   "<p style='margin:5px 0; color:#666;'>" + r.address + "</p>"
             +   "<p style='margin:5px 0; color:#666;'>" + r.contact + "</p>"
             + "</td>"
             + "<td style='text-align:right; vertical-align:top;'>"
             +   "<p style='color:#999; margin:0; font-size:12px;'>STAY DETAILS</p>"
             +   "<p style='margin:10px 0; color:#333;'><b>Room:</b> " + r.roomType + "</p>"
             +   "<p style='margin:5px 0; color:#333;'><b>Check-In:</b> " + r.checkIn + "</p>"
             +   "<p style='margin:5px 0; color:#333;'><b>Check-Out:</b> " + r.checkOut + "</p>"
             +   "<p style='margin:5px 0; color:#333;'><b>Duration:</b> " + nights + " Night(s)</p>"
             + "</td></tr></table>"
             + "<table style='width:100%; border-collapse:collapse; margin-bottom:30px;'>"
             + "<thead><tr style='background:#f5f5f5;'>"
             +   "<th style='padding:15px; text-align:left;  border-bottom:2px solid #1976D2;'>Description</th>"
             +   "<th style='padding:15px; text-align:center;border-bottom:2px solid #1976D2;'>Nights</th>"
             +   "<th style='padding:15px; text-align:right; border-bottom:2px solid #1976D2;'>Rate</th>"
             +   "<th style='padding:15px; text-align:right; border-bottom:2px solid #1976D2;'>Amount</th>"
             + "</tr></thead><tbody>"
             + "<tr>"
             +   "<td style='padding:15px; border-bottom:1px solid #eee;'>Accommodation<br>"
             +       "<small style='color:#999;'>" + r.roomType + " Room</small></td>"
             +   "<td style='padding:15px; text-align:center; border-bottom:1px solid #eee;'>" + nights + "</td>"
             +   "<td style='padding:15px; text-align:right;  border-bottom:1px solid #eee;'>LKR "
             +       String.format("%,d", rate) + "</td>"
             +   "<td style='padding:15px; text-align:right;  border-bottom:1px solid #eee;'><b>LKR "
             +       String.format("%,d", total) + "</b></td>"
             + "</tr></tbody></table>"
             + "<div style='text-align:right; padding:25px; background:#f9f9f9; border-radius:8px;'>"
             +   "<p style='color:#666; margin:0; font-size:14px;'>TOTAL AMOUNT</p>"
             +   "<h1 style='color:#1976D2; margin:10px 0 0 0; font-size:36px;'>LKR "
             +       String.format("%,d", total) + "</h1>"
             + "</div>"
             + "<p style='text-align:center; margin-top:40px; font-size:12px; color:#999;'>"
             +   "Thank you for choosing Ocean Resort! Questions? Call +94 11 234 5678"
             + "</p></div></body></html>";
    }
}
