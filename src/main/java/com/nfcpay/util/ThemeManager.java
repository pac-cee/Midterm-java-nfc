package com.nfcpay.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.Window;

/**
 * Theme Manager for Dark/Light mode toggle
 */
public class ThemeManager {
    private static boolean isDarkMode = false;
    
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }
    
    public static void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
        applyTheme();
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    private static void applyTheme() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            // Update all open windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}