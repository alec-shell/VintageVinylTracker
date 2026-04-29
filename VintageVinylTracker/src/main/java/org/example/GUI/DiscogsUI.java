package org.example.GUI;

import org.example.Client.ProxyClient;
import org.example.Configurable.Constants;
import org.example.Controller.APIController;
import org.example.DTO.Record;
import org.example.GUI.async.AsyncCalls;
import org.example.Repository.DatabaseRepository;
import org.example.GUI.statsUpdate.EventTriggers;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class DiscogsUI extends JPanel {
    private final JTable discogsTable;
    private final DiscogsTableModel tableModel = new DiscogsTableModel();
    private final JTextField artistNameJTF = new JTextField();
    private final JTextField albumNameJTF =  new JTextField();
    private final JTextField yearJTF = new JTextField();
    private final JTextField catNoJTF =  new JTextField();
    private JLabel albumArtLabel;
    private final JLabel pricingInfoLabel = new JLabel();

    private final ProxyClient proxyClient;
    private final DatabaseRepository dbAccess;
    private final EventTriggers eventTriggers;
    private final AsyncCalls asyncCalls;

    private ArrayList<Record> records;
    private final HashMap<String, Double> selectionPrices = new HashMap<>();
    private final HashMap<Integer, String> cachedPricing = new HashMap<>();
    private final HashMap<Integer, ImageIcon> cachedImgs =  new HashMap<>();

    private final Record noResultRecord = new Record(0, "NO RESULTS", "NO RESULTS",
            "NO RESULTS", "NO RESULTS", "NO RESULTS", null, false,
            0.0, 0.0, null);

    public DiscogsUI(ProxyClient proxyClient, DatabaseRepository dbAccess, EventTriggers eventTriggers,
                     AsyncCalls asyncCalls) {
        this.proxyClient = proxyClient;
        this.dbAccess = dbAccess;
        this.eventTriggers = eventTriggers;
        this.asyncCalls = asyncCalls;
        this.setLayout(new BorderLayout());
        JPanel UIPanel = buildUIPanel();
        this.add(UIPanel, BorderLayout.NORTH);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        discogsTable = new JTable(tableModel);
        discogsTable.setRowSorter(sorter);
        addTableListener();
        this.add(new JScrollPane(discogsTable), BorderLayout.CENTER);
    } // constructor

    private void addTableListener() {
        discogsTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) { return; }
            int viewRow = discogsTable.getSelectedRow();
            if  (viewRow < 0) { return; }
            int rowIndex = discogsTable.convertRowIndexToModel(viewRow);
            if (records == null || records.size() <= rowIndex) { return; }
            Record record = records.get(rowIndex);
            if (cachedImgs.containsKey(record.getID())) {
                albumArtLabel.setIcon(cachedImgs.get(record.getID()));
            } else {
                asyncCalls.asyncThumbnailCall(record.getThumbUrl(), albumArtLabel, cachedImgs, record.getID());
            }
            if (cachedPricing.containsKey(record.getID())
                    && !cachedPricing.get(record.getID()).equals("Unavailable")) {
                pricingInfoLabel.setText(cachedPricing.get(record.getID()));
            } else {
                asyncCalls.asyncPricingCall(proxyClient, record.getID(), pricingInfoLabel,
                        selectionPrices, cachedPricing);
            }
        });
    } // addTableListener()

    private JPanel buildUIPanel() {
        JPanel temp = new JPanel(new  GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        temp.setBackground(Constants.bgColor);
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
        if (records == null || records.size() <= tableIndex || tableIndex < 0) { return; }
        int modelRow =  discogsTable.convertRowIndexToModel(tableIndex);
        Record selectedRecord = records.get(modelRow);
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
                JOptionPane.showMessageDialog(this,
                        "Invalid price entry",
                        "Error: ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        selected.setPurchasePrice(price);

        String condition = (String) JOptionPane.showInputDialog(this,
                "Select Album Condition:",
                "Condition",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Constants.pricingConditions,
                Constants.pricingConditions[0]
        );
        selected.setCondition(condition);
        selected.setValue(selectionPrices.get(condition));
        if (sendAlbumToDB(selected)) eventTriggers.updateStatsUI();
    } // addOwnedAlbum()

    private boolean sendAlbumToDB(Record selected) {
        boolean added = dbAccess.addRecordEntry(
                selected.getID(),
                selected.getArtistName(),
                selected.getAlbumName(),
                selected.getYear(),
                selected.getCountry(),
                selected.getCatNo(),
                selected.getThumbUrl(),
                selected.isOwned(),
                selected.getPurchasePrice(),
                selected.getValue(),
                selected.getCondition()
        );
        if (!added) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add record to database.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            JOptionPane.showMessageDialog(this,
                    "Record added to database",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    } // sendAlbumToDB()

    private JPanel buildAlbumInfoDisplay() {
        JPanel albumInfoPanel = new JPanel();
        albumInfoPanel.setBackground(Constants.bgColor);
        albumInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        int gridxCounter = 0;
        c.gridy = 0;
        albumArtLabel = new JLabel(Constants.defaultThumbNail);
        albumArtLabel.setPreferredSize(new Dimension(Constants.albumArtWidth, Constants.albumArtHeight));
        c.gridx = gridxCounter++;
        albumInfoPanel.add(albumArtLabel, c);
        c.gridx = gridxCounter;
        pricingInfoLabel.setPreferredSize(new Dimension(300, 180));
        albumInfoPanel.add(pricingInfoLabel, c);
        return albumInfoPanel;
    } // buildAlbumInfoDisplay()

    private JPanel buildSearchEntryForm() {
        JPanel searchEntryForm = new JPanel();
        searchEntryForm.setBackground(Constants.bgColor);
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
        submit.addActionListener(_ -> submitActionListener());
        return searchEntryForm;
    } // buildSearchEntryForm()

    private void submitActionListener() {
        if (searchDiscogs()) {
            albumArtLabel.setIcon(Constants.defaultThumbNail);
            pricingInfoLabel.setText("");
            cachedImgs.clear();
            cachedPricing.clear();
        }
    } // submitActionListener()

    private boolean searchDiscogs() {
        String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
        String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
        String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
        String catNo = !catNoJTF.getText().isBlank() ? catNoJTF.getText() : null;
        albumArtLabel.setIcon(Constants.defaultThumbNail);
        pricingInfoLabel.setText("");
        if (artist == null && album == null && year == null && catNo == null) {
            pricingInfoLabel.setText("Please fill out at least one form field.");
            return false;
        }
        else {
            asyncSearchQueryCall(album, artist, year, catNo);
            return true;
        }
    } // searchDiscogs()

    private JPanel getEntryField(String fieldName, JTextField entryField) {
        JPanel entryPanel = new JPanel();
        entryPanel.setBackground(Constants.bgColor);
        JLabel entryLabel = new JLabel(fieldName);
        entryLabel.setPreferredSize(new Dimension(100, 20));
        entryField.setPreferredSize(new Dimension(200, 20));
        entryPanel.add(entryLabel);
        entryPanel.add(entryField);
        return entryPanel;
    } // getEntryField()

    private void asyncSearchQueryCall(String album, String artist, String year, String catNo) {
        SwingWorker<Void, Object> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                records = APIController.getDiscogsSearchResults(proxyClient, album, artist, year, catNo);
                if (records.isEmpty()) {records.add(noResultRecord);}
                return null;
            } // doInBackground()

            @Override
            protected void done() {
                    tableModel.fireTableDataChanged();
            } // done()
        };
        worker.execute();
    } // asyncSearchQueryCall()

    private class DiscogsTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return records == null ? 0 : records.size();
        }

        @Override
        public int getColumnCount() {
            return Constants.discogsColumnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> records.get(rowIndex).getCatNo();
                case 1 -> records.get(rowIndex).getArtistName();
                case 2 -> records.get(rowIndex).getAlbumName();
                case 3 -> records.get(rowIndex).getYear();
                default -> records.get(rowIndex).getCountry();
            };
        }

        @Override
        public String getColumnName(int column) {
            return Constants.discogsColumnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) {return Integer.class;}
            return String.class;
        }

    } // CustomTableModel

} // DiscogsUI
