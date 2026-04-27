package org.example.GUI;

import org.example.Client.ProxyClient;
import org.example.Config.Constants;
import org.example.Controller.APIController;
import org.example.DTO.Record;
import org.example.GUI.async.AsyncCalls;
import org.example.Service.DBAccessService;
import org.example.GUI.statsUpdate.EventTriggers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class DiscogsUI extends JPanel {
    private final JTable discogsTable;
    private final JTextField artistNameJTF = new JTextField();
    private final JTextField albumNameJTF =  new JTextField();
    private final JTextField yearJTF = new JTextField();
    private final JTextField catNoJTF =  new JTextField();
    private JLabel albumArtLabel;
    private final JLabel pricingInfoLabel = new JLabel();
    private final ProxyClient proxyClient;
    private final DBAccessService dbAccess;
    private final EventTriggers eventTriggers;
    private final AsyncCalls asyncCalls;
    private ArrayList<Record> records;
    private final HashMap<String, Double> selectionPrices = new HashMap<>();

    public DiscogsUI(ProxyClient proxyClient, DBAccessService dbAccess, EventTriggers eventTriggers,
                     AsyncCalls asyncCalls) {
        this.proxyClient = proxyClient;
        this.dbAccess = dbAccess;
        this.eventTriggers = eventTriggers;
        this.asyncCalls = asyncCalls;
        this.setLayout(new BorderLayout());
        JPanel UIPanel = buildUIPanel();
        this.add(UIPanel, BorderLayout.NORTH);
        TableModel model = new DefaultTableModel(Constants.discogsColumnNames, 0);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        discogsTable = new JTable(model);
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
            asyncCalls.asyncThumbnailCall(records.get(rowIndex).getThumbUrl(), albumArtLabel);
            asyncCalls.asyncPricingCall(proxyClient, records.get(rowIndex).getID(), pricingInfoLabel, selectionPrices);
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
                selected.getPurchasePrice(),
                selected.getValue(),
                selected.getCondition()
        );
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
            eventTriggers.updateStatsUI();
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
        searchDiscogs();
        albumArtLabel.setIcon(Constants.defaultThumbNail);
        pricingInfoLabel.setText("");
    } // submitActionListener()

    private void searchDiscogs() {
        String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
        String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
        String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
        String catNo = !catNoJTF.getText().isBlank() ? catNoJTF.getText() : null;
        albumArtLabel.setIcon(Constants.defaultThumbNail);
        pricingInfoLabel.setText("");
        if (artist == null && album == null && year == null && catNo == null) {
            pricingInfoLabel.setText("Please fill out at least one form field.");
        }
        else asyncSearchQueryCall(album, artist, year, catNo);
    } // searchDiscogs()

    private void displayResults() {
        DefaultTableModel model = (DefaultTableModel) discogsTable.getModel();
        model.setRowCount(0);
        if (records.isEmpty()) {
            pricingInfoLabel.setText("No results found.");
            model.addRow(new String[]{"NO RESULTS", "NO RESULTS", "NO RESULTS", "NO RESULTS", "NO RESULTS"});
        }
        else {
            for (Record record : records) {
                model.addRow(new String[]{record.getCatNo(),
                        record.getArtistName(),
                        record.getAlbumName(),
                        record.getYear(),
                        record.getCountry()
                });
            }
        }
    } // displayResults()

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
        SwingWorker<ArrayList<Record>, Object> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Record> doInBackground() {
                records = APIController.getDiscogsSearchResults(proxyClient, album, artist, year, catNo);
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
