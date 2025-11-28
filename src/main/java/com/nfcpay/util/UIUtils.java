package com.nfcpay.util;

import javax.swing.*;
import java.awt.*;

/**
 * UI Utility Methods with Professional Design System
 */
public class UIUtils {
    
    // Professional Color Palette
    public static final Color PRIMARY = new Color(0x2563eb);     // Professional Blue
    public static final Color SUCCESS = new Color(0x059669);     // Success Green
    public static final Color WARNING = new Color(0xd97706);     // Warning Orange
    public static final Color DANGER = new Color(0xdc2626);      // Error Red
    public static final Color NEUTRAL = new Color(0x64748b);     // Text Gray
    
    // Background Colors
    public static final Color BG_LIGHT = new Color(0xf8fafc);    // Light Background
    public static final Color SURFACE_LIGHT = new Color(0xffffff); // Light Surface
    public static final Color TEXT_COLOR = new Color(0x1f2937);   // Text Color
    public static final Color BORDER_COLOR = new Color(0xe2e8f0); // Border Color
    
    // Spacing System (16px grid)
    public static final int SPACING_XS = 8;
    public static final int SPACING_SM = 16;
    public static final int SPACING_MD = 24;
    public static final int SPACING_LG = 32;
    public static final int SPACING_XL = 48;
    
    // Typography
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean showConfirmation(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public static void centerWindow(Window window) {
        window.setLocationRelativeTo(null);
    }
    
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(FONT_BODY);
        button.setPreferredSize(new Dimension(140, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // Professional Button Creators
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY);
    }
    
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS);
    }
    
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER);
    }
    
    // Professional Panel Creator
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        ));
        return card;
    }
    
    // Header Panel with Gradient
    public static JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY.brighter());
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    // Simple color getters
    public static Color getBackgroundColor() {
        return BG_LIGHT;
    }
    
    public static Color getSurfaceColor() {
        return SURFACE_LIGHT;
    }
    
    public static Color getTextColor() {
        return TEXT_COLOR;
    }
    
    public static Color getBorderColor() {
        return BORDER_COLOR;
    }
}