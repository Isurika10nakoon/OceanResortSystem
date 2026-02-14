package resort;

import java.awt.*;
import java.awt.event.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.time.LocalDate;

public class ViewReservations {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public ViewReservations(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showViewReservations();
    }

    private void showViewReservations() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
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
        
        ModernButton refreshBtn = new ModernButton("REFRESH", mainSystem.INFO_COLOR, true);
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
        table.setSelectionBackground(new Color(mainSystem.PRIMARY_COLOR.getRed(), mainSystem.PRIMARY_COLOR.getGreen(), 
                                                mainSystem.PRIMARY_COLOR.getBlue(), 40));
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
                statusColor = mainSystem.SUCCESS_COLOR;
            } else if (status.equals("Upcoming")) {
                statusColor = mainSystem.WARNING_COLOR;
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
            
            Dimension btnSize = new Dimension(45, 35); 
            Font btnFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);

            JButton viewBtn = new JButton("👁");
            viewBtn.setPreferredSize(btnSize); 
            viewBtn.setFont(btnFont);         
            viewBtn.setToolTipText("View Details");
            styleActionButton(viewBtn, mainSystem.INFO_COLOR);

            JButton deleteBtn = new JButton("🗑");
            deleteBtn.setPreferredSize(btnSize);
            deleteBtn.setFont(btnFont);
            deleteBtn.setToolTipText("Delete");
            styleActionButton(deleteBtn, mainSystem.DANGER_COLOR);
            
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
                
                Dimension btnSize = new Dimension(45, 35);
                Font btnFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);

                JButton viewBtn = new JButton("👁");
                viewBtn.setPreferredSize(btnSize);
                viewBtn.setFont(btnFont);
                viewBtn.setToolTipText("View Details");
                styleActionButton(viewBtn, mainSystem.INFO_COLOR);
                viewBtn.addActionListener(e -> showReservationDetails(resNo));

                JButton deleteBtn = new JButton("🗑");
                deleteBtn.setPreferredSize(btnSize);
                deleteBtn.setFont(btnFont);
                deleteBtn.setToolTipText("Delete");
                styleActionButton(deleteBtn, mainSystem.DANGER_COLOR);
                
                deleteBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Delete this reservation?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        mainSystem.getReservations().remove(resNo);
                        mainSystem.saveReservationsToFile();
                        refreshTable(model, null);
                        JOptionPane.showMessageDialog(null, "✓ Deleted successfully!");
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
        HashMap<String, OceanResortSystem.Reservation> reservations = mainSystem.getReservations();
        for (OceanResortSystem.Reservation r : reservations.values()) {
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
        OceanResortSystem.Reservation r = mainSystem.getReservations().get(resNo);
        if (r == null) return;
        
        JDialog dialog = new JDialog((Frame)null, "Reservation Details", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 25));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(35, 35, 35, 35));
        
        JLabel titleLabel = new JLabel("📋 Reservation Details");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        titleLabel.setForeground(mainSystem.PRIMARY_COLOR);
        
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
        int rate = mainSystem.getRoomRate(r.roomType);
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
}
