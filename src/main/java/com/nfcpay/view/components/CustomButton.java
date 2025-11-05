package com.nfcpay.view.components;

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
        setFont(new Font("Arial", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 35));
    }
    
    public static CustomButton createPrimaryButton(String text) {
        return new CustomButton(text, new Color(0, 123, 255));
    }
    
    public static CustomButton createSuccessButton(String text) {
        return new CustomButton(text, new Color(40, 167, 69));
    }
    
    public static CustomButton createDangerButton(String text) {
        return new CustomButton(text, new Color(220, 53, 69));
    }
    
    public static CustomButton createSecondaryButton(String text) {
        return new CustomButton(text, new Color(108, 117, 125));
    }
    
    private void applyStyle(ButtonStyle style) {
        switch (style) {
            case PRIMARY:
                setBackground(new Color(0, 123, 255));
                break;
            case SUCCESS:
                setBackground(new Color(40, 167, 69));
                break;
            case DANGER:
                setBackground(new Color(220, 53, 69));
                break;
            case WARNING:
                setBackground(new Color(255, 193, 7));
                setForeground(Color.BLACK);
                break;
            case SECONDARY:
                setBackground(new Color(108, 117, 125));
                break;
        }
    }
}