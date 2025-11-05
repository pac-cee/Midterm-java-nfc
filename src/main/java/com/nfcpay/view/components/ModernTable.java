package com.nfcpay.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernTable extends JTable {
    private TableRowSorter<ModernTableModel<?>> sorter;
    
    public ModernTable(ModernTableModel<?> model) {
        super(model);
        setupTable();
        setupSorting();
        setupStyling();
    }
    
    private void setupTable() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(35);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        
        // Enable auto-resize
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Add hover effect
        addMouseMotionListener(new MouseAdapter() {
            private int hoveredRow = -1;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
        });
    }
    
    private void setupSorting() {
        sorter = new TableRowSorter<>((ModernTableModel<?>) getModel());
        setRowSorter(sorter);
    }
    
    private void setupStyling() {
        // Header styling
        JTableHeader header = getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        
        // Cell renderer
        setDefaultRenderer(Object.class, new ModernCellRenderer());
    }
    
    public void filterTable(String text) {
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    private class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(new Color(52, 152, 219, 100));
                c.setForeground(Color.BLACK);
            } else if (row % 2 == 0) {
                c.setBackground(new Color(248, 249, 250));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setFont(new Font("SansSerif", Font.PLAIN, 11));
            
            return c;
        }
    }
}