package com.nfcpay.view.components;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private Timer fadeTimer;
    
    public StatusPanel() {
        setLayout(new BorderLayout(10, 0));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        setPreferredSize(new Dimension(0, 35));
        
        createComponents();
    }
    
    private void createComponents() {
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(52, 73, 94));
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(100, 20));
        progressBar.setStringPainted(true);
        
        add(statusLabel, BorderLayout.WEST);
        add(progressBar, BorderLayout.EAST);
    }
    
    public void showStatus(String message, StatusType type) {
        statusLabel.setText(message);
        
        switch (type) {
            case SUCCESS:
                statusLabel.setForeground(new Color(39, 174, 96));
                break;
            case ERROR:
                statusLabel.setForeground(new Color(231, 76, 60));
                break;
            case WARNING:
                statusLabel.setForeground(new Color(243, 156, 18));
                break;
            case INFO:
            default:
                statusLabel.setForeground(new Color(52, 73, 94));
                break;
        }
        
        // Auto-fade after 3 seconds
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(3000, e -> {
            statusLabel.setText("Ready");
            statusLabel.setForeground(new Color(52, 73, 94));
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }
    
    public void showProgress(String message, int progress) {
        statusLabel.setText(message);
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");
        progressBar.setVisible(true);
        
        if (progress >= 100) {
            Timer hideTimer = new Timer(1000, e -> progressBar.setVisible(false));
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }
    
    public void hideProgress() {
        progressBar.setVisible(false);
    }
    
    public enum StatusType {
        SUCCESS, ERROR, WARNING, INFO
    }
}