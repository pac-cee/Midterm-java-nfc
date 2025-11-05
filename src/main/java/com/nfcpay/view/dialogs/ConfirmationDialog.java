package com.nfcpay.view.dialogs;

import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmationDialog extends JDialog {
    private boolean confirmed = false;
    
    public ConfirmationDialog(Frame parent, String title, String message, String confirmText) {
        super(parent, title, true);
        initializeDialog(message, confirmText);
    }
    
    private void initializeDialog(String message, String confirmText) {
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Icon and message panel
        JPanel messagePanel = new JPanel(new BorderLayout(15, 15));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messagePanel.setBackground(Color.WHITE);
        
        // Warning icon
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(60, 60));
        
        // Message
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; width: 250px;'>" + 
                                        message + "</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 221, 222)));
        
        CustomButton cancelButton = new CustomButton("Cancel", CustomButton.ButtonStyle.SECONDARY);
        CustomButton confirmButton = new CustomButton(confirmText, CustomButton.ButtonStyle.DANGER);
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        add(messagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set focus to confirm button
        getRootPane().setDefaultButton(confirmButton);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public static boolean showConfirmation(Component parent, String title, String message, String confirmText) {
        Frame parentFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        ConfirmationDialog dialog = new ConfirmationDialog(parentFrame, title, message, confirmText);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}