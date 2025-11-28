package com.nfcpay.view.components;

import com.nfcpay.util.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Custom styled button component
 */
public class CustomButton extends JButton {
    
    public enum ButtonStyle {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY
    }
    
    public CustomButton(String text) {
        super(text);
        setupButton();
    }
    
    public CustomButton(String text, ButtonStyle style) {
        super(text);
        applyStyle(style);
        setupButton();
    }
    
    public CustomButton(String text, Color backgroundColor) {
        super(text);
        setBackground(backgroundColor);
        setupButton();
    }
    
    private void setupButton() {
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(UIUtils.FONT_BODY);
        setBorder(BorderFactory.createEmptyBorder(UIUtils.SPACING_XS, UIUtils.SPACING_SM, UIUtils.SPACING_XS, UIUtils.SPACING_SM));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, 45));
        
        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (isEnabled()) {
                    setBackground(originalColor.darker());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (isEnabled()) {
                    setBackground(originalColor);
                }
            }
        });
    }
    
    public static CustomButton createPrimaryButton(String text) {
        return new CustomButton(text, ButtonStyle.PRIMARY);
    }
    
    public static CustomButton createSuccessButton(String text) {
        return new CustomButton(text, ButtonStyle.SUCCESS);
    }
    
    public static CustomButton createDangerButton(String text) {
        return new CustomButton(text, ButtonStyle.DANGER);
    }
    
    public static CustomButton createSecondaryButton(String text) {
        return new CustomButton(text, ButtonStyle.SECONDARY);
    }
    
    public static CustomButton createWarningButton(String text) {
        return new CustomButton(text, ButtonStyle.WARNING);
    }
    
    private void applyStyle(ButtonStyle style) {
        switch (style) {
            case PRIMARY:
                setBackground(UIUtils.PRIMARY);
                break;
            case SUCCESS:
                setBackground(UIUtils.SUCCESS);
                break;
            case DANGER:
                setBackground(UIUtils.DANGER);
                break;
            case WARNING:
                setBackground(UIUtils.WARNING);
                setForeground(Color.BLACK);
                break;
            case SECONDARY:
                setBackground(UIUtils.NEUTRAL);
                break;
        }
    }
}