package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class DBSearchTabUI extends JPanel {
    private JTable resultsDisplay;
    private DBAccess dbAccess;
    private JTextField albumNameJTF = new JTextField();
    private JTextField artistNameJTF =  new JTextField();
    private JTextField yearJTF = new JTextField();
    private JCheckBox ownedSelector = null;
    private final String[] columnNames = new String[]{"Album", "Band", "Year", "Owned",
            "Condition", "Price", "IsMaster", "ID"};


    public DBSearchTabUI(DBAccess dbAccess) {
        this.dbAccess = dbAccess;
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        resultsDisplay = new JTable(model);
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new BorderLayout());
        this.add(buildSearchEntryForm(true, null), BorderLayout.NORTH);
        this.add(new JScrollPane(resultsDisplay), BorderLayout.CENTER);
    } // constructor


    private JPanel buildSearchEntryForm(boolean displaysOwnedSelector, Boolean defaultOwned) {
        JPanel searchEntryForm = new JPanel();
        searchEntryForm.setBackground(Color.LIGHT_GRAY);
        searchEntryForm.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        int gridyCounter = 0;
        JLabel searchLabel = new JLabel("Search in local database:");
        c.gridy = gridyCounter++;
        searchEntryForm.add(searchLabel, c);
        JPanel artistName = getEntryField("Artist Name: ", artistNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(artistName, c);
        JPanel albumName =  getEntryField("Album Name: ", albumNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(albumName, c);
        JPanel yearField = getEntryField("Year: ", yearJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(yearField, c);
        if  (displaysOwnedSelector) {
            ownedSelector = new JCheckBox(" --- Owned?");
            c.gridy = gridyCounter++;
            searchEntryForm.add(ownedSelector, c);
        }
        JButton submit =  new JButton("Search");
        c.gridy = gridyCounter;
        searchEntryForm.add(submit, c);
        submit.addActionListener(e -> {
            String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
            String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
            String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
            boolean isOwned = ownedSelector != null ? ownedSelector.isSelected() : defaultOwned;
            Record[] results = queryDB(artist, album, year,  isOwned);
            updateResultsDisplay(results);
        });
        return searchEntryForm;
    } // buildSearchEntryForm()


    private JPanel getEntryField(String fieldName, JTextField entryField) {
        JPanel entryPanel = new JPanel();
        entryPanel.setBackground(Color.LIGHT_GRAY);
        JLabel entryLabel = new JLabel(fieldName);
        entryLabel.setPreferredSize(new Dimension(100, 20));
        entryField.setPreferredSize(new Dimension(200, 20));
        entryPanel.add(entryLabel);
        entryPanel.add(entryField);
        return entryPanel;
    } // getEntryField()


    private void updateResultsDisplay(Record[] rows) {
        DefaultTableModel model = (DefaultTableModel) resultsDisplay.getModel();
        model.setRowCount(0);
        for (Record record : rows) {
            model.addRow(record.returnValues());
        }
    } // updateResultsDisplay()


    private Record[] queryDB(String name, String album, String year, Boolean isOwned) {
        List<Record> results = dbAccess.searchEntries(name, album, year, isOwned);
        Record[] rows = new Record[results.size()];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = results.get(i);
        }
        return rows;
    } // queryDB()


} // DBSearchTabUI class
