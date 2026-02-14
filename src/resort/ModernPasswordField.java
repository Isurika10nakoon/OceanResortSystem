package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

class ModernPasswordField extends JPasswordField {
    private boolean showPassword = false;
    private JButton toggleButton;
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public ModernPasswordField() {
        setOpaque(false);
        setFont(MAIN_FONT);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 50));
        
        toggleButton = new JButton("👁");
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        toggleButton.addActionListener(e -> {
            showPassword = !showPassword;
            setEchoChar(showPassword ? '\u0000' : '●');
        });
        
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(toggleButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
        
        if (hasFocus()) {
            g2.setColor(PRIMARY_COLOR);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(new Color(224, 224, 224));
            g2.setStroke(new BasicStroke(1));
        }
        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 10, 10));
        
        super.paintComponent(g);
        g2.dispose();
    }
}
