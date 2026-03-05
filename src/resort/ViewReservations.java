package resort;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class ViewReservations {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;
    private DefaultTableModel tableModel;

    public ViewReservations(OceanResortSystem system, JPanel content) {
        this.mainSystem  = system;
        this.contentArea = content;
        showViewReservations();
    }

    /* ══════════════════════════════════════════════════════════
       MAIN PANEL
    ══════════════════════════════════════════════════════════ */
    private void showViewReservations() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(mainSystem.BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // ── Header row ──────────────────────────────────────────
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

        headerPanel.add(header,      BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // ── Table ────────────────────────────────────────────────
        String[] columns = {"ID", "Guest Name", "Room", "Check-In", "Check-Out", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 6; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(56);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(
                mainSystem.PRIMARY_COLOR.getRed(),
                mainSystem.PRIMARY_COLOR.getGreen(),
                mainSystem.PRIMARY_COLOR.getBlue(), 40));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        // Table header style
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(66, 66, 66));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 50));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // Status column badge renderer
        table.getColumn("Status").setCellRenderer((tbl, value, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(value.toString(), JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(6, 12, 6, 12));
            Color c;
            switch (value.toString()) {
                case "Active":    c = mainSystem.SUCCESS_COLOR; break;
                case "Upcoming":  c = mainSystem.WARNING_COLOR; break;
                default:          c = new Color(158, 158, 158); break;
            }
            lbl.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
            lbl.setForeground(c);
            return lbl;
        });

        // Actions column — renderer (display only, shows the buttons visually)
        table.getColumn("Actions").setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 9));
            p.setOpaque(false);
            p.add(makeActionBtn("✏  Edit",   mainSystem.INFO_COLOR));
            p.add(makeActionBtn("🗑  Delete", mainSystem.DANGER_COLOR));
            return p;
        });

        // Actions column — editor (clickable)
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object val,
                    boolean sel, int row, int col) {

                String resNo = (String) tbl.getValueAt(row, 0);

                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 9));
                p.setOpaque(false);

                JButton editBtn   = makeActionBtn("✏  Edit",   mainSystem.INFO_COLOR);
                JButton deleteBtn = makeActionBtn("🗑  Delete", mainSystem.DANGER_COLOR);

                editBtn.addActionListener(ev -> {
                    stopCellEditing();
                    showEditReservationDialog(resNo);
                });

                deleteBtn.addActionListener(ev -> {
                    stopCellEditing();
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete reservation " + resNo + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = mainSystem.deleteReservation(resNo);
                        if (deleted) {
                            JOptionPane.showMessageDialog(null,
                                "✓ Reservation " + resNo + " deleted successfully!",
                                "Deleted", JOptionPane.INFORMATION_MESSAGE);
                            refreshTable(null);
                        }
                    }
                });

                p.add(editBtn);
                p.add(deleteBtn);
                return p;
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(180);

        refreshTable(null);

        // Search
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String term = searchField.getText().toLowerCase().trim();
                refreshTable(term.isEmpty() ? null : term);
            }
        });

        // Refresh button — reloads from DB
        refreshBtn.addActionListener(e -> {
            mainSystem.loadReservationsFromDB();
            searchField.setText("");
            refreshTable(null);
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scroll,      BorderLayout.CENTER);

        panel.add(contentPanel);
        contentArea.add(panel);
    }

    /* ══════════════════════════════════════════════════════════
       EDIT RESERVATION DIALOG
    ══════════════════════════════════════════════════════════ */
    private void showEditReservationDialog(String resNo) {
        OceanResortSystem.Reservation r = mainSystem.getReservations().get(resNo);
        if (r == null) return;

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Reservation — " + resNo);
        dialog.setSize(520, 560);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(mainSystem.BG_COLOR);
        outer.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // ── Fields ──────────────────────────────────────────────
        // Reservation No — read-only
        ModernTextField tfResNo = new ModernTextField();
        tfResNo.setText(r.resNo);
        tfResNo.setEditable(false);
        tfResNo.setForeground(Color.GRAY);

        ModernTextField tfName = new ModernTextField();
        tfName.setPlaceholder("Guest full name");
        tfName.setText(r.name);

        ModernTextField tfAddr = new ModernTextField();
        tfAddr.setPlaceholder("Guest address");
        tfAddr.setText(r.address);

        ModernTextField tfCont = new ModernTextField();
        tfCont.setPlaceholder("+94 XX XXX XXXX");
        tfCont.setText(r.contact);

        JComboBox<String> roomBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        roomBox.setFont(mainSystem.MAIN_FONT);
        roomBox.setBackground(Color.WHITE);
        roomBox.setSelectedItem(r.roomType);

        // Date spinners pre-filled from existing reservation
        SpinnerDateModel checkInModel = new SpinnerDateModel(
            java.sql.Date.valueOf(r.checkIn), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkInSpinner = new JSpinner(checkInModel);
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkInSpinner.setFont(mainSystem.MAIN_FONT);

        SpinnerDateModel checkOutModel = new SpinnerDateModel(
            java.sql.Date.valueOf(r.checkOut), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkOutSpinner = new JSpinner(checkOutModel);
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setFont(mainSystem.MAIN_FONT);

        // Tag fields
        tfName      .setName("name");
        tfAddr      .setName("address");
        tfCont      .setName("contact");

        int row = 0;
        addFormRow(grid, gbc, "Reservation No",   tfResNo,         row++);
        addFormRow(grid, gbc, "Guest Full Name *", tfName,          row++);
        addFormRow(grid, gbc, "Address *",         tfAddr,          row++);
        addFormRow(grid, gbc, "Contact Number *",  tfCont,          row++);
        addFormRow(grid, gbc, "Room Type *",       roomBox,         row++);
        addFormRow(grid, gbc, "Check-In Date *",   checkInSpinner,  row++);
        addFormRow(grid, gbc, "Check-Out Date *",  checkOutSpinner, row++);

        // ── Update button ────────────────────────────────────────
        ModernButton updateBtn = new ModernButton("UPDATE RESERVATION", mainSystem.SUCCESS_COLOR);
        updateBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        grid.add(updateBtn, gbc);

        outer.add(grid, BorderLayout.CENTER);
        dialog.add(outer, BorderLayout.CENTER);

        updateBtn.addActionListener(e -> {
            // Validation
            boolean valid = true;
            if (tfName.getText().trim().isEmpty()) { tfName.setError(true); valid = false; }
            if (tfAddr.getText().trim().isEmpty()) { tfAddr.setError(true); valid = false; }
            if (tfCont.getText().trim().isEmpty()) { tfCont.setError(true); valid = false; }

            if (!valid) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill all required fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                java.util.Date inDate  = (java.util.Date) checkInSpinner.getValue();
                java.util.Date outDate = (java.util.Date) checkOutSpinner.getValue();

                LocalDate checkIn  = inDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOut = outDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Check-out date must be after check-in date!",
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update in DB
                boolean updated = mainSystem.updateReservation(
                    resNo,
                    tfName.getText().trim(),
                    tfAddr.getText().trim(),
                    tfCont.getText().trim(),
                    roomBox.getSelectedItem().toString(),
                    checkIn, checkOut
                );

                if (updated) {
                    JOptionPane.showMessageDialog(dialog,
                        "✓ Reservation " + resNo + " updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    mainSystem.loadReservationsFromDB();  // sync cache
                    refreshTable(null);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        dialog.setVisible(true);
    }

    /* ══════════════════════════════════════════════════════════
       REFRESH TABLE FROM MEMORY CACHE
    ══════════════════════════════════════════════════════════ */
    private void refreshTable(String filter) {
        tableModel.setRowCount(0);
        for (OceanResortSystem.Reservation r : mainSystem.getReservations().values()) {
            r.updateStatus();
            if (filter == null
                    || r.name.toLowerCase().contains(filter)
                    || r.resNo.toLowerCase().contains(filter)
                    || r.contact.toLowerCase().contains(filter)) {
                tableModel.addRow(new Object[]{
                    r.resNo, r.name, r.roomType,
                    r.checkIn, r.checkOut, r.status, ""
                });
            }
        }
    }

    /* ══════════════════════════════════════════════════════════
       HELPERS
    ══════════════════════════════════════════════════════════ */
    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        field.setPreferredSize(new Dimension(300, 46));
        panel.add(field, gbc);
    }

    private JButton makeActionBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(color);
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        btn.setBorder(BorderFactory.createLineBorder(color, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(95, 32));
        return btn;
    }
}
