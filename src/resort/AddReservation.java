package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AddReservation {
    private OceanResortSystem mainSystem;
    private JPanel contentArea;

    public AddReservation(OceanResortSystem system, JPanel content) {
        this.mainSystem = system;
        this.contentArea = content;
        showAddReservation();
    }

    private void showAddReservation() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainSystem.BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        JLabel header = new JLabel("New Reservation");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(new Color(33, 33, 33));
        
        JPanel formCard = new JPanel() {
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
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(35, 40, 35, 40));
        formCard.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        ModernTextField resNo = new ModernTextField();
        resNo.setPlaceholder("e.g., RES-001");
        ModernTextField name = new ModernTextField();
        name.setPlaceholder("Guest full name");
        ModernTextField addr = new ModernTextField();
        addr.setPlaceholder("Guest address");
        ModernTextField cont = new ModernTextField();
        cont.setPlaceholder("+94 XX XXX XXXX");
        
        JComboBox<String> room = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        room.setFont(mainSystem.MAIN_FONT);
        room.setBackground(Color.WHITE);
        
        SpinnerDateModel checkInModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkInSpinner = new JSpinner(checkInModel);
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkInSpinner.setFont(mainSystem.MAIN_FONT);
        
        SpinnerDateModel checkOutModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner checkOutSpinner = new JSpinner(checkOutModel);
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setFont(mainSystem.MAIN_FONT);
        
        int row = 0;
        addFormRow(formCard, gbc, "Reservation Number *", resNo, row++);
        addFormRow(formCard, gbc, "Guest Full Name *", name, row++);
        addFormRow(formCard, gbc, "Address *", addr, row++);
        addFormRow(formCard, gbc, "Contact Number *", cont, row++);
        addFormRow(formCard, gbc, "Room Type *", room, row++);
        addFormRow(formCard, gbc, "Check-In Date *", checkInSpinner, row++);
        addFormRow(formCard, gbc, "Check-Out Date *", checkOutSpinner, row++);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttonPanel.setOpaque(false);
        
        ModernButton clearBtn = new ModernButton("CLEAR", new Color(117, 117, 117));
        clearBtn.setPreferredSize(new Dimension(120, 46));
        clearBtn.addActionListener(e -> {
            resNo.setText("");
            name.setText("");
            addr.setText("");
            cont.setText("");
            room.setSelectedIndex(0);
            checkInSpinner.setValue(new Date());
            checkOutSpinner.setValue(new Date());
        });
        
        ModernButton saveBtn = new ModernButton("SAVE RESERVATION", mainSystem.SUCCESS_COLOR);
        saveBtn.setPreferredSize(new Dimension(180, 46));
        saveBtn.addActionListener(e -> {
            boolean valid = true;
            if (resNo.getText().trim().isEmpty()) { resNo.setError(true); valid = false; }
            if (name.getText().trim().isEmpty()) { name.setError(true); valid = false; }
            if (addr.getText().trim().isEmpty()) { addr.setError(true); valid = false; }
            if (cont.getText().trim().isEmpty()) { cont.setError(true); valid = false; }
            
            if (!valid) {
                JOptionPane.showMessageDialog(null, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Date inDate = (Date) checkInSpinner.getValue();
                Date outDate = (Date) checkOutSpinner.getValue();

                LocalDate checkIn = inDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate checkOut = outDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                if (!checkOut.isAfter(checkIn)) {
                    JOptionPane.showMessageDialog(null, "Check-out must be after check-in!", "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (mainSystem.getReservations().containsKey(resNo.getText())) {
                    JOptionPane.showMessageDialog(null, "Reservation number already exists!", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                mainSystem.getReservations().put(resNo.getText(), new OceanResortSystem.Reservation(
                    resNo.getText(), name.getText(), addr.getText(), cont.getText(),
                    room.getSelectedItem().toString(), checkIn, checkOut
                ));

                mainSystem.saveReservationsToFile();
                JOptionPane.showMessageDialog(null, "✓ Reservation saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearBtn.doClick();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saving reservation!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(clearBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 8, 8, 8);
        formCard.add(buttonPanel, gbc);
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setOpaque(false);
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(formCard, BorderLayout.CENTER);
        
        panel.add(contentPanel);
        contentArea.add(panel);
    }
    
    private void addFormRow(JPanel container, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        container.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        component.setPreferredSize(new Dimension(400, 50));
        container.add(component, gbc);
    }
}
