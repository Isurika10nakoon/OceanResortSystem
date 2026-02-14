package resort;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

class ModernButton extends JButton {
    private Color baseColor;
    private boolean isIconButton = false;
    
    public ModernButton(String text) {
        this(text, new Color(25, 118, 210));
    }
    
    public ModernButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        setupButton();
    }
    
    public ModernButton(String text, Color color, boolean iconButton) {
        super(text);
        this.baseColor = color;
        this.isIconButton = iconButton;
        setupButton();
    }
    
    private void setupButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (!isIconButton) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fill(new RoundRectangle2D.Double(2, 2, getWidth() - 2, getHeight() - 2, 12, 12));
        }
        
        if (getModel().isPressed()) {
            g2.setColor(darken(baseColor, 0.2f));
        } else if (getModel().isRollover()) {
            g2.setColor(brighten(baseColor, 0.15f));
        } else {
            g2.setColor(baseColor);
        }
        
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - (isIconButton ? 0 : 2), 
                                            getHeight() - (isIconButton ? 0 : 2), 12, 12));
        super.paintComponent(g);
        g2.dispose();
    }
    
    private Color brighten(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }
    
    private Color darken(Color color, float factor) {
        int r = (int)(color.getRed() * (1 - factor));
        int g = (int)(color.getGreen() * (1 - factor));
        int b = (int)(color.getBlue() * (1 - factor));
        return new Color(r, g, b);
    }
}
