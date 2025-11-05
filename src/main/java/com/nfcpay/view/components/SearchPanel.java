package com.nfcpay.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JButton clearButton;
    
    public SearchPanel(String placeholder) {
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        createComponents(placeholder);
        layoutComponents();
    }
    
    private void createComponents(String placeholder) {
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setToolTipText(placeholder);
        
        searchButton = new CustomButton("Search", CustomButton.ButtonStyle.PRIMARY);
        searchButton.setPreferredSize(new Dimension(80, 35));
        
        clearButton = new CustomButton("Clear", CustomButton.ButtonStyle.SECONDARY);
        clearButton.setPreferredSize(new Dimension(70, 35));
    }
    
    private void layoutComponents() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        add(searchField, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }
    
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
        searchField.addActionListener(listener);
    }
    
    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }
    
    public String getSearchText() {
        return searchField.getText().trim();
    }
    
    public void clearSearch() {
        searchField.setText("");
    }
    
    public void setSearchText(String text) {
        searchField.setText(text);
    }
}