package com.nfcpay.view.components;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModernTableModel<T> extends AbstractTableModel {
    private final String[] columnNames;
    private final List<T> allData;
    private List<T> filteredData;
    private final DataExtractor<T> dataExtractor;
    
    public interface DataExtractor<T> {
        Object[] extractData(T item);
    }
    
    public ModernTableModel(String[] columnNames, DataExtractor<T> dataExtractor) {
        this.columnNames = columnNames;
        this.dataExtractor = dataExtractor;
        this.allData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return filteredData.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < filteredData.size()) {
            Object[] data = dataExtractor.extractData(filteredData.get(rowIndex));
            return columnIndex < data.length ? data[columnIndex] : "";
        }
        return "";
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void setData(List<T> data) {
        this.allData.clear();
        this.allData.addAll(data);
        this.filteredData.clear();
        this.filteredData.addAll(data);
        fireTableDataChanged();
    }
    
    public void addItem(T item) {
        allData.add(item);
        filteredData.add(item);
        fireTableRowsInserted(filteredData.size() - 1, filteredData.size() - 1);
    }
    
    public void updateItem(int index, T item) {
        if (index >= 0 && index < allData.size()) {
            allData.set(index, item);
            applyCurrentFilter();
        }
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < filteredData.size()) {
            T item = filteredData.get(index);
            allData.remove(item);
            filteredData.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public T getItemAt(int rowIndex) {
        return rowIndex >= 0 && rowIndex < filteredData.size() ? filteredData.get(rowIndex) : null;
    }
    
    public void filter(Predicate<T> predicate) {
        filteredData = allData.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        fireTableDataChanged();
    }
    
    public void clearFilter() {
        filteredData.clear();
        filteredData.addAll(allData);
        fireTableDataChanged();
    }
    
    private void applyCurrentFilter() {
        // Reapply current filter if needed
        fireTableDataChanged();
    }
    
    public List<T> getAllData() {
        return new ArrayList<>(allData);
    }
    
    public List<T> getFilteredData() {
        return new ArrayList<>(filteredData);
    }
}