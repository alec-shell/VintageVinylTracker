package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class DBSearchUI extends JPanel {
    private final JTable dbTable;
    private final DBAccess dbAccess;
    private final GenerateStats collectionStats;
    private final EventTriggers eventTriggers;
    private final JTextField albumNameJTF = new JTextField();
    private final JTextField artistNameJTF =  new JTextField();
    private final JTextField yearJTF = new JTextField();
    private final JTextField catNoJTF = new JTextField();
    private JCheckBox ownedSelector = null;
    private final String[] columnNames = new String[]{
            "Catalog No.",
            "Artist",
            "Album",
            "Year",
            "Country",
            "Owned",
            "Condition"
    };
    private ArrayList<Record> records;
    private JLabel albumArtLabel;
    private final JLabel pricingInfoLabel = new JLabel();
    private final DiscogsAuthorization discogsAuth;

    public DBSearchUI( DiscogsAuthorization discogsAuth, DBAccess dbAccess, GenerateStats collectionStats, EventTriggers eventTriggers) {
        this.dbAccess = dbAccess;
        this.discogsAuth = discogsAuth;
        this.collectionStats = collectionStats;
        this.eventTriggers = eventTriggers;
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        dbTable = new JTable(model);
        addTableListener();
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new BorderLayout());
        this.add(buildUIPanel(), BorderLayout.NORTH);
        this.add(new JScrollPane(dbTable), BorderLayout.CENTER);
    } // constructor

    private void addTableListener() {
        dbTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowIndex = dbTable.getSelectedRow();
                if (records == null || records.size() <= rowIndex) { return; }
                AsyncCalls.asyncThumbnailCall(records.get(rowIndex).getThumbUrl(), albumArtLabel);
                AsyncCalls.asyncPricingCall(records.get(rowIndex).getID(), pricingInfoLabel, discogsAuth);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    } // addTableListener()

    private JPanel buildUIPanel() {
        JPanel temp = new JPanel(new  GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        temp.setBackground(Color.LIGHT_GRAY);
        temp.add(buildAlbumInfoDisplay(), c);
        c.gridx = 1;
        temp.add(buildSearchEntryForm(), c);
        c.gridx = 0;
        c.gridy = 1;
        JButton addBtn = new JButton("Remove Album");
        addBtn.addActionListener(_ -> removeAlbumListener());
        temp.add(addBtn, c);
        return temp;
    } // buildUIPanel()

    private void removeAlbumListener() {
        int selectedIndex = dbTable.getSelectedRow();
        if (records == null || records.size() <= selectedIndex) { return; }
        boolean deleted = dbAccess.deleteRecordEntry(records.get(selectedIndex).getID());
        if (deleted) {
            records.remove(selectedIndex);
            updateResultsDisplay(records);
            albumArtLabel.setIcon(AsyncCalls.defaultThumbNail);
            pricingInfoLabel.setText("");
            collectionStats.parseOwnedAlbums();
            eventTriggers.updateStatsUI();
        } else {
            JOptionPane.showMessageDialog(this, "Album could not be deleted",
                    "Error",
                    JOptionPane.OK_OPTION);
        }
    } // removeAlbumListener()

    private JPanel buildAlbumInfoDisplay() {
        JPanel albumInfoPanel = new JPanel();
        albumInfoPanel.setBackground(Color.LIGHT_GRAY);
        albumInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        int gridxCounter = 0;
        c.gridy = 0;
        albumArtLabel = new JLabel(AsyncCalls.defaultThumbNail);
        albumArtLabel.setPreferredSize(new Dimension(AsyncCalls.albumArtWidth, AsyncCalls.albumArtHeight));
        c.gridx = gridxCounter++;
        albumInfoPanel.add(albumArtLabel, c);
        c.gridx = gridxCounter;
        pricingInfoLabel.setPreferredSize(new Dimension(300, 180));
        albumInfoPanel.add(pricingInfoLabel, c);
        return albumInfoPanel;
    } // build albumInfoDisplay()

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
        JPanel albumName =  getEntryField("Album Name: ", albumNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(albumName, c);
        JPanel yearField = getEntryField("Year: ", yearJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(yearField, c);
        JPanel catalogNo = getEntryField("Catalog No.", catNoJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(catalogNo, c);
        ownedSelector = new JCheckBox(" --- Owned");
        c.gridy = gridyCounter++;
        searchEntryForm.add(ownedSelector, c);
        JButton submit =  new JButton("Search");
        c.gridy = gridyCounter;
        searchEntryForm.add(submit, c);
        submit.addActionListener(_ -> submitActionListener());
        return searchEntryForm;
    } // buildSearchEntryForm()

    private void submitActionListener() {
        String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
        String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
        String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
        String catNo = !catNoJTF.getText().isBlank() ? catNoJTF.getText() : null;
        records = dbAccess.searchRecordEntries(artist, album, year, catNo, ownedSelector.isSelected());
        updateResultsDisplay(records);
    } // submitActionListener()

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

    private void updateResultsDisplay(ArrayList<Record> rows) {
        DefaultTableModel model = (DefaultTableModel) dbTable.getModel();
        model.setRowCount(0);
        for (Record record : rows) {
            model.addRow(new String[]{record.getCatNo(),
                    record.getArtistName(),
                    record.getAlbumName(),
                    record.getYear(),
                    record.getCountry(),
                    Boolean.toString(record.isOwned()),
                    record.getCondition()
            });
        }
    } // updateResultsDisplay()

} // DBSearchTabUI class
