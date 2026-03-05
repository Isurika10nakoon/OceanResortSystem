package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

public class ManageStaff {

    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    // Table model — refreshed from DB each time
    private DefaultTableModel tableModel;

    public ManageStaff(OceanResortSystem system, JPanel content) {
        this.mainSystem  = system;
        this.contentArea = content;
        showManageStaff();
    }

    /* ══════════════════════════════════════════════════════════
       MAIN PANEL
    ══════════════════════════════════════════════════════════ */
    private void showManageStaff() {

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(mainSystem.BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // ── Header row ──────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel header = new JLabel("Manage Staff");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));

        ModernButton addBtn = new ModernButton("+ ADD NEW STAFF", mainSystem.SUCCESS_COLOR);
        addBtn.setPreferredSize(new Dimension(180, 45));
        addBtn.addActionListener(e -> showAddStaffDialog());

        headerPanel.add(header,  BorderLayout.WEST);
        headerPanel.add(addBtn,  BorderLayout.EAST);

        // ── Table ────────────────────────────────────────────────
        String[] columns = {"Staff ID", "Full Name", "Username", "Role", "Contact", "Email", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return col == 6; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(52);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(25, 118, 210, 40));

        // Header style
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(new Color(66, 66, 66));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 50));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        // Role badge renderer
        table.getColumn("Role").setCellRenderer((tbl, value, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(value.toString(), JLabel.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(4, 10, 4, 10));
            if ("Admin".equals(value.toString())) {
                lbl.setBackground(new Color(25, 118, 210, 30));
                lbl.setForeground(new Color(25, 118, 210));
            } else {
                lbl.setBackground(new Color(76, 175, 80, 30));
                lbl.setForeground(new Color(76, 175, 80));
            }
            return lbl;
        });

        // Actions column — renderer (display only)
        table.getColumn("Actions").setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            p.setOpaque(false);
            JButton e = makeActionBtn("✏ Edit",   mainSystem.INFO_COLOR);
            JButton d = makeActionBtn("🗑 Delete", mainSystem.DANGER_COLOR);
            p.add(e); p.add(d);
            return p;
        });

        // Actions column — editor (clickable)
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable tbl, Object val,
                    boolean sel, int row, int col) {

                String staffId = (String) tbl.getValueAt(row, 0);

                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
                p.setOpaque(false);

                JButton editBtn   = makeActionBtn("✏ Edit",   mainSystem.INFO_COLOR);
                JButton deleteBtn = makeActionBtn("🗑 Delete", mainSystem.DANGER_COLOR);

                editBtn.addActionListener(ev -> {
                    stopCellEditing();
                    showEditStaffDialog(staffId);
                });

                deleteBtn.addActionListener(ev -> {
                    stopCellEditing();
                    int confirm = JOptionPane.showConfirmDialog(null,
                        "Delete staff member " + staffId + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteStaff(staffId);
                    }
                });

                p.add(editBtn); p.add(deleteBtn);
                return p;
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scroll,      BorderLayout.CENTER);
        contentArea.add(panel);

        // Load data from DB
        loadStaffTable();
    }

    /* ══════════════════════════════════════════════════════════
       LOAD ALL STAFF FROM DB INTO TABLE
    ══════════════════════════════════════════════════════════ */
    private void loadStaffTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT staff_id, full_name, username, role, contact, email FROM staff ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("staff_id"),
                    rs.getString("full_name"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("contact"),
                    rs.getString("email"),
                    ""   // placeholder for Actions column
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error loading staff:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ══════════════════════════════════════════════════════════
       ADD STAFF DIALOG
    ══════════════════════════════════════════════════════════ */
    private void showAddStaffDialog() {

        JDialog dialog = new JDialog();
        dialog.setTitle("Add New Staff Member");
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = buildStaffForm(null); // null = add mode
        dialog.add(formPanel, BorderLayout.CENTER);

        // ── Save button inside form ──────────────────────────────
        JButton saveBtn = findSaveButton(formPanel);
        if (saveBtn != null) {
            saveBtn.addActionListener(e -> {
                boolean saved = saveNewStaff(formPanel);
                if (saved) {
                    dialog.dispose();
                    loadStaffTable();   // refresh table
                }
            });
        }

        dialog.setVisible(true);
    }

    /* ══════════════════════════════════════════════════════════
       EDIT STAFF DIALOG
    ══════════════════════════════════════════════════════════ */
    private void showEditStaffDialog(String staffId) {

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Staff — " + staffId);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = buildStaffForm(staffId); // staffId = edit mode, prefills fields
        dialog.add(formPanel, BorderLayout.CENTER);

        JButton saveBtn = findSaveButton(formPanel);
        if (saveBtn != null) {
            saveBtn.setText("UPDATE STAFF");
            saveBtn.addActionListener(e -> {
                boolean updated = updateStaff(staffId, formPanel);
                if (updated) {
                    dialog.dispose();
                    loadStaffTable();
                }
            });
        }

        dialog.setVisible(true);
    }

    /* ══════════════════════════════════════════════════════════
       BUILD STAFF FORM  (shared by Add + Edit)
    ══════════════════════════════════════════════════════════ */
    private JPanel buildStaffForm(String staffId) {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(mainSystem.BG_COLOR);
        outer.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Fields
        ModernTextField tfStaffId  = new ModernTextField(); tfStaffId.setPlaceholder("e.g. STF-004");
        ModernTextField tfFullName = new ModernTextField(); tfFullName.setPlaceholder("Full name");
        ModernTextField tfUsername = new ModernTextField(); tfUsername.setPlaceholder("Login username");
        ModernTextField tfPassword = new ModernTextField(); tfPassword.setPlaceholder("Password");
        ModernTextField tfContact  = new ModernTextField(); tfContact.setPlaceholder("+94 XX XXX XXXX");
        ModernTextField tfEmail    = new ModernTextField(); tfEmail.setPlaceholder("email@example.com");
        JComboBox<String> roleBox  = new JComboBox<>(new String[]{"Staff", "Admin"});
        roleBox.setFont(mainSystem.MAIN_FONT);
        roleBox.setBackground(Color.WHITE);

        // Tag fields so we can find them later
        tfStaffId .setName("staffId");
        tfFullName.setName("fullName");
        tfUsername.setName("username");
        tfPassword.setName("password");
        tfContact .setName("contact");
        tfEmail   .setName("email");
        roleBox   .setName("role");

        // If editing, pre-fill values from DB
        if (staffId != null) {
            fillFormFromDB(staffId, tfStaffId, tfFullName, tfUsername,
                           tfPassword, tfContact, tfEmail, roleBox);
            tfStaffId.setEditable(false);   // don't allow changing the ID
            tfStaffId.setForeground(Color.GRAY);
        }

        int row = 0;
        addRow(grid, gbc, "Staff ID *",   tfStaffId,  row++);
        addRow(grid, gbc, "Full Name *",  tfFullName, row++);
        addRow(grid, gbc, "Username *",   tfUsername, row++);
        addRow(grid, gbc, "Password *",   tfPassword, row++);
        addRow(grid, gbc, "Role *",       roleBox,    row++);
        addRow(grid, gbc, "Contact",      tfContact,  row++);
        addRow(grid, gbc, "Email",        tfEmail,    row++);

        // Save button — listener attached by caller
        ModernButton saveBtn = new ModernButton("SAVE STAFF", mainSystem.SUCCESS_COLOR);
        saveBtn.setName("saveBtn");
        saveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 46));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        grid.add(saveBtn, gbc);

        outer.add(grid, BorderLayout.CENTER);
        return outer;
    }

    /* ══════════════════════════════════════════════════════════
       PRE-FILL FORM FROM DB (edit mode)
    ══════════════════════════════════════════════════════════ */
    private void fillFormFromDB(String staffId,
            ModernTextField tfId, ModernTextField tfName,
            ModernTextField tfUser, ModernTextField tfPass,
            ModernTextField tfContact, ModernTextField tfEmail,
            JComboBox<String> roleBox) {

        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfId     .setText(rs.getString("staff_id"));
                tfName   .setText(rs.getString("full_name"));
                tfUser   .setText(rs.getString("username"));
                tfPass   .setText(rs.getString("password"));
                tfContact.setText(rs.getString("contact"));
                tfEmail  .setText(rs.getString("email"));
                roleBox.setSelectedItem(rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ══════════════════════════════════════════════════════════
       INSERT NEW STAFF INTO DB
    ══════════════════════════════════════════════════════════ */
    private boolean saveNewStaff(JPanel formPanel) {

        String staffId  = getField(formPanel, "staffId");
        String fullName = getField(formPanel, "fullName");
        String username = getField(formPanel, "username");
        String password = getField(formPanel, "password");
        String contact  = getField(formPanel, "contact");
        String email    = getField(formPanel, "email");
        String role     = getComboValue(formPanel, "role");

        // Validation
        if (staffId.isEmpty() || fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Please fill Staff ID, Full Name, Username and Password!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO staff (staff_id, full_name, username, password, role, contact, email) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffId);
            ps.setString(2, fullName);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.setString(6, contact);
            ps.setString(7, email);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                "✓ Staff member " + fullName + " added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // duplicate entry
                JOptionPane.showMessageDialog(null,
                    "Staff ID or Username already exists!",
                    "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Error adding staff:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
            return false;
        }
    }

    /* ══════════════════════════════════════════════════════════
       UPDATE EXISTING STAFF IN DB
    ══════════════════════════════════════════════════════════ */
    private boolean updateStaff(String staffId, JPanel formPanel) {

        String fullName = getField(formPanel, "fullName");
        String username = getField(formPanel, "username");
        String password = getField(formPanel, "password");
        String contact  = getField(formPanel, "contact");
        String email    = getField(formPanel, "email");
        String role     = getComboValue(formPanel, "role");

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Full Name, Username and Password are required!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE staff SET full_name=?, username=?, password=?, role=?, contact=?, email=? "
                   + "WHERE staff_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.setString(5, contact);
            ps.setString(6, email);
            ps.setString(7, staffId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null,
                "✓ Staff member " + fullName + " updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error updating staff:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /* ══════════════════════════════════════════════════════════
       DELETE STAFF FROM DB
    ══════════════════════════════════════════════════════════ */
    private void deleteStaff(String staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staffId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,
                "Staff member " + staffId + " deleted.",
                "Deleted", JOptionPane.INFORMATION_MESSAGE);
            loadStaffTable();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error deleting staff:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ══════════════════════════════════════════════════════════
       HELPER UTILITIES
    ══════════════════════════════════════════════════════════ */

    /** Adds a label + field row to a GridBagLayout panel */
    private void addRow(JPanel panel, GridBagConstraints gbc,
                        String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        field.setPreferredSize(new Dimension(260, 46));
        panel.add(field, gbc);
    }

    /** Gets trimmed text from a named ModernTextField inside a panel */
    private String getField(JPanel panel, String name) {
        for (Component c : getAllComponents(panel)) {
            if (c instanceof ModernTextField && name.equals(c.getName())) {
                return ((ModernTextField) c).getText().trim();
            }
        }
        return "";
    }

    /** Gets selected value from a named JComboBox inside a panel */
    private String getComboValue(JPanel panel, String name) {
        for (Component c : getAllComponents(panel)) {
            if (c instanceof JComboBox && name.equals(c.getName())) {
                return ((JComboBox<?>) c).getSelectedItem().toString();
            }
        }
        return "Staff";
    }

    /** Finds the save button by name inside a panel */
    private JButton findSaveButton(JPanel panel) {
        for (Component c : getAllComponents(panel)) {
            if (c instanceof JButton && "saveBtn".equals(c.getName())) {
                return (JButton) c;
            }
        }
        return null;
    }

    /** Recursively collects all child components from a container */
    private java.util.List<Component> getAllComponents(Container container) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        for (Component c : container.getComponents()) {
            list.add(c);
            if (c instanceof Container) {
                list.addAll(getAllComponents((Container) c));
            }
        }
        return list;
    }

    /** Creates a small styled action button */
    private JButton makeActionBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(color);
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        btn.setBorder(BorderFactory.createLineBorder(color, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(85, 30));
        return btn;
    }
}
