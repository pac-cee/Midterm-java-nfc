package com.nfcpay.view.dialogs;

import com.nfcpay.controller.MainController;
import com.nfcpay.model.User;
import com.nfcpay.util.Session;
import com.nfcpay.util.UIUtils;
import com.nfcpay.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User Profile Dialog
 */
public class ProfileDialog extends JDialog {
    private final MainController mainController;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private CustomButton updateButton;
    private CustomButton changePasswordButton;
    private CustomButton cancelButton;
    
    public ProfileDialog(JFrame parent, MainController mainController) {
        super(parent, "My Profile", true);
        this.mainController = mainController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        loadUserData();
    }
    
    private void initializeComponents() {
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        updateButton = CustomButton.createPrimaryButton("Update Profile");
        changePasswordButton = CustomButton.createSecondaryButton("Change Password");
        cancelButton = CustomButton.createSecondaryButton("Cancel");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Profile Tab
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        profilePanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        profilePanel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        profilePanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        profilePanel.add(emailField, gbc);
        
        JPanel profileButtonPanel = new JPanel(new FlowLayout());
        profileButtonPanel.add(updateButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        profilePanel.add(profileButtonPanel, gbc);
        
        // Password Tab
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        passwordPanel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordPanel.add(currentPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        passwordPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordPanel.add(newPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        passwordPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordPanel.add(confirmPasswordField, gbc);
        
        JPanel passwordButtonPanel = new JPanel(new FlowLayout());
        passwordButtonPanel.add(changePasswordButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        passwordPanel.add(passwordButtonPanel, gbc);
        
        tabbedPane.addTab("Profile", profilePanel);
        tabbedPane.addTab("Password", passwordPanel);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(cancelButton);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        updateButton.addActionListener(this::handleUpdateProfile);
        changePasswordButton.addActionListener(this::handleChangePassword);
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void setupDialog() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void loadUserData() {
        User user = Session.getCurrentUser();
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
    }
    
    private void handleUpdateProfile(ActionEvent e) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (fullName.isEmpty() || email.isEmpty()) {
            UIUtils.showError(this, "Please fill in all fields");
            return;
        }
        
        try {
            User updatedUser = mainController.getAuthController().updateProfile(
                Session.getCurrentUser().getUserId(), fullName, email);
            Session.setCurrentUser(updatedUser);
            UIUtils.showSuccess(this, "Profile updated successfully!");
            
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to update profile: " + ex.getMessage());
        }
    }
    
    private void handleChangePassword(ActionEvent e) {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            UIUtils.showError(this, "Please fill in all password fields");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            UIUtils.showError(this, "New passwords do not match");
            return;
        }
        
        try {
            mainController.getAuthController().changePassword(
                Session.getCurrentUser().getUserId(), currentPassword, newPassword);
            UIUtils.showSuccess(this, "Password changed successfully!");
            
            // Clear password fields
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            
        } catch (Exception ex) {
            UIUtils.showError(this, "Failed to change password: " + ex.getMessage());
        }
    }
}