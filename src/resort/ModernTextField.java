package resort;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

class ModernTextField extends JTextField {
    private String placeholder = "";
    private boolean hasError = false;
    private final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private final Color DANGER_COLOR = new Color(244, 67, 54);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public ModernTextField() {
        setOpaque(false);
        setFont(MAIN_FONT);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public void setError(boolean error) {
        this.hasError = error;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
        
        if (hasError) {
            g2.setColor(DANGER_COLOR);
            g2.setStroke(new BasicStroke(2));
        } else if (hasFocus()) {
            g2.setColor(PRIMARY_COLOR);
            g2.setStroke(new BasicStroke(2));
        } else {
            g2.setColor(new Color(224, 224, 224));
            g2.setStroke(new BasicStroke(1));
        }
        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 10, 10));
        
        super.paintComponent(g);
        
        if (getText().isEmpty() && !placeholder.isEmpty() && !hasFocus()) {
            g2.setColor(new Color(158, 158, 158));
            g2.setFont(getFont());
            g2.drawString(placeholder, 16, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
        }
        
        g2.dispose();
    }
}
