package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DiscogsUI extends JPanel {
    private JTable discogsTable;
    private JTextField artistNameJTF = new JTextField();
    private JTextField albumNameJTF =  new JTextField();
    private JTextField yearJTF = new JTextField();
    private JTextField catNoJTF =  new JTextField();
    private String[] columnNames = {"Catalog No.", "Artist", "Album", "Year", "Country"};
    private DiscogsAuthorization discogsAuth;

    protected DiscogsUI(DiscogsAuthorization discogsAuth) {
        this.discogsAuth = discogsAuth;
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        this.setLayout(new BorderLayout());
        this.add(buildSearchEntryForm(), BorderLayout.NORTH);
        discogsTable = new JTable(model);
        this.add(new JScrollPane(discogsTable), BorderLayout.CENTER);
    } // constructor

    private JPanel buildSearchEntryForm() {
        JPanel searchEntryForm = new JPanel();
        searchEntryForm.setBackground(Color.LIGHT_GRAY);
        searchEntryForm.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        int gridyCounter = 0;
        JLabel searchLabel = new JLabel("Search:");
        c.gridy = gridyCounter++;
        searchEntryForm.add(searchLabel, c);
        JPanel artistName = getEntryField("Artist Name: ", artistNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(artistName, c);
        JPanel albumName = getEntryField("Album Name: ", albumNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(albumName, c);
        JPanel yearField = getEntryField("Year: ", yearJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(yearField, c);
        JPanel catNo = getEntryField("Catalog No: ", catNoJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(catNo, c);
        JButton submit =  new JButton("Search");
        c.gridy = gridyCounter;
        searchEntryForm.add(submit, c);
        submit.addActionListener(_ -> searchDiscogs());
        return searchEntryForm;
    } // buildSearchEntryForm()

    private void searchDiscogs() {
        String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
        String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
        String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
        String catNo = !catNoJTF.getText().isBlank() ? catNoJTF.getText() : null;
        ArrayList<Record> results = ParseAPIResponse.buildSearchQueryCollection(discogsAuth, album, artist, year, catNo);
        displayResults(results);
    } // searchDiscogs()

    private void displayResults(ArrayList<Record> results) {
        DefaultTableModel model = (DefaultTableModel) discogsTable.getModel();
        model.setRowCount(0);
        for (Record record : results) {
            model.addRow(new String[]{record.getCatNo(),
            record.getArtistName(),
            record.getAlbumName(),
            record.getYear(),
            record.getCountry()});
        }
    }

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

} // DiscogsUI class
