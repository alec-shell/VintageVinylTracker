package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class DiscogsUI extends JPanel {
    private final JTable discogsTable;
    private final JTextField artistNameJTF = new JTextField();
    private final JTextField albumNameJTF =  new JTextField();
    private final JTextField yearJTF = new JTextField();
    private final JTextField catNoJTF =  new JTextField();
    private final String[] columnNames = {
            "Catalog No.",
            "Artist",
            "Album",
            "Year",
            "Country"
    };
    private JLabel albumArtLabel;
    private final JLabel pricingInfoLabel = new JLabel();
    private final DiscogsAuthorization discogsAuth;
    private final DBAccess dbAccess;
    private ArrayList<Record> records;

    protected DiscogsUI(DiscogsAuthorization discogsAuth, DBAccess dbAccess) {
        this.discogsAuth = discogsAuth;
        this.dbAccess = dbAccess;
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        this.setLayout(new BorderLayout());
        JPanel UIPanel = buildUIPanel();
        this.add(UIPanel, BorderLayout.NORTH);
        discogsTable = new JTable(model);
        addTableListener();
        this.add(new JScrollPane(discogsTable), BorderLayout.CENTER);
    } // constructor

    private void addTableListener() {
        discogsTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowIndex = discogsTable.getSelectedRow();
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
        JButton addBtn = new JButton("Add Album");
        addBtn.addActionListener(_ -> addAlbumActionListener());
        temp.add(addBtn, c);
        return temp;
    } // buildUIPanel()

    private void addAlbumActionListener() {
        int tableIndex = discogsTable.getSelectedRow();
        if (records == null || records.size() <= tableIndex) { return; }
        Record selectedRecord = records.get(tableIndex);
        int owned = JOptionPane.showConfirmDialog(this,
                "Do you own this album?",
                "Add Album",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (owned == JOptionPane.YES_OPTION) {
            selectedRecord.setIsOwned(true);
            addOwnedAlbum(selectedRecord);
        }
        else if (owned == JOptionPane.NO_OPTION) {
            sendAlbumToDB(selectedRecord);
        }
    } // addAlbumActionListener()

    private void addOwnedAlbum(Record selected) {
        Double price = null;
        while (price == null) {
            try {
                price = Double.parseDouble(JOptionPane.showInputDialog(this,
                        "Purchase Price: "));
            } catch (NumberFormatException e) {
                JOptionPane.showConfirmDialog(this,
                        "Invalid price entry",
                        "Error: ",
                        JOptionPane.OK_OPTION);
            }
        }
        selected.setPurchasePrice(price);
        sendAlbumToDB(selected);
    } // addOwnedAlbum()

    private void sendAlbumToDB(Record selected) {
        boolean added = dbAccess.addRecordEntry(
                selected.getID(),
                selected.getArtistName(),
                selected.getAlbumName(),
                selected.getYear(),
                selected.getCountry(),
                selected.getCatNo(),
                selected.getThumbUrl(),
                selected.isOwned(),
                selected.getPurchasePrice());
        if (!added) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add record to database.",
                    "Error",
                JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Record added to database",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    } // sendAlbumToDB()

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
    } // buildAlbumInfoDisplay()

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
        albumArtLabel.setIcon(AsyncCalls.defaultThumbNail);
        pricingInfoLabel.setText("");
        asyncSearchQueryCall(album, artist, year, catNo);
    } // searchDiscogs()

    private void displayResults() {
        DefaultTableModel model = (DefaultTableModel) discogsTable.getModel();
        model.setRowCount(0);
        for (Record record : records) {
            model.addRow(new String[]{record.getCatNo(),
            record.getArtistName(),
            record.getAlbumName(),
            record.getYear(),
            record.getCountry()
            });
        }
    } // displayResults()

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

    private void asyncSearchQueryCall(String album, String artist, String year, String catNo) {
        SwingWorker<Object, Void> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() {
                records = ParseAPIResponse.buildSearchQueryCollection(discogsAuth, album, artist, year, catNo);
                return null;
            } // doInBackground()

            @Override
            protected void done() {
                    displayResults();
            } // done()
        };
        worker.execute();
    } // asyncSearchQueryCall()

} // DiscogsUI class
