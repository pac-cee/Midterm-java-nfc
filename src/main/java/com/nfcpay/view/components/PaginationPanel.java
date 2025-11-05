package com.nfcpay.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PaginationPanel extends JPanel {
    private JButton firstButton, prevButton, nextButton, lastButton;
    private JLabel pageLabel;
    private JComboBox<Integer> pageSizeCombo;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalItems = 0;
    private int pageSize = 10;
    
    public PaginationPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        createComponents();
        updateButtons();
    }
    
    private void createComponents() {
        firstButton = new JButton("<<");
        prevButton = new JButton("<");
        nextButton = new JButton(">");
        lastButton = new JButton(">>");
        
        // Style buttons
        JButton[] buttons = {firstButton, prevButton, nextButton, lastButton};
        for (JButton button : buttons) {
            button.setPreferredSize(new Dimension(40, 30));
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setBackground(new Color(52, 73, 94));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
        }
        
        pageLabel = new JLabel("Page 1 of 1");
        pageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        pageSizeCombo = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        pageSizeCombo.setSelectedItem(pageSize);
        pageSizeCombo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        
        add(new JLabel("Items per page:"));
        add(pageSizeCombo);
        add(Box.createHorizontalStrut(20));
        add(firstButton);
        add(prevButton);
        add(pageLabel);
        add(nextButton);
        add(lastButton);
    }
    
    public void updatePagination(int totalItems) {
        this.totalItems = totalItems;
        this.totalPages = Math.max(1, (int) Math.ceil((double) totalItems / pageSize));
        
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }
        
        pageLabel.setText(String.format("Page %d of %d (%d items)", 
            currentPage, totalPages, totalItems));
        updateButtons();
    }
    
    private void updateButtons() {
        firstButton.setEnabled(currentPage > 1);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
        lastButton.setEnabled(currentPage < totalPages);
    }
    
    public void addNavigationListeners(ActionListener first, ActionListener prev, 
                                     ActionListener next, ActionListener last) {
        firstButton.addActionListener(first);
        prevButton.addActionListener(prev);
        nextButton.addActionListener(next);
        lastButton.addActionListener(last);
    }
    
    public void addPageSizeListener(ActionListener listener) {
        pageSizeCombo.addActionListener(listener);
    }
    
    public void goToFirstPage() {
        currentPage = 1;
        updateButtons();
    }
    
    public void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateButtons();
        }
    }
    
    public void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateButtons();
        }
    }
    
    public void goToLastPage() {
        currentPage = totalPages;
        updateButtons();
    }
    
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return (Integer) pageSizeCombo.getSelectedItem(); }
    public int getTotalPages() { return totalPages; }
    
    public void setPageSize(int size) {
        pageSize = size;
        pageSizeCombo.setSelectedItem(size);
    }
}