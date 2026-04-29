package org.example.GUI;

import org.example.Client.ProxyClient;
import org.example.Configurable.Constants;
import org.example.DTO.Record;
import org.example.GUI.async.AsyncCalls;
import org.example.Repository.DatabaseRepository;
import org.example.GUI.statsUpdate.EventTriggers;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBSearchUI extends JPanel {
    private final JTable dbTable;
    private final CustomTableModel tableModel =  new CustomTableModel();
    private final DatabaseRepository dbAccess;
    private final EventTriggers eventTriggers;
    private final JTextField albumNameJTF = new JTextField();
    private final JTextField artistNameJTF =  new JTextField();
    private final JTextField yearJTF = new JTextField();
    private final JTextField catNoJTF = new JTextField();
    private final ButtonGroup ownedBtnGroup =  new ButtonGroup();
    private JLabel albumArtLabel;
    private final JLabel pricingInfoLabel = new JLabel();

    private final ProxyClient proxyClient;
    private final AsyncCalls asyncCalls;

    private ArrayList<Record> records;
    private final HashMap<String, Double> selectionPrices = new HashMap<>();
    private final HashMap<Integer, String> cachedPricing = new HashMap<>();
    private final HashMap<Integer, ImageIcon> cachedImgs =  new HashMap<>();

    public DBSearchUI(ProxyClient proxyClient, DatabaseRepository dbAccess, EventTriggers eventTriggers,
                      AsyncCalls asyncCalls) {
        this.dbAccess = dbAccess;
        this.proxyClient = proxyClient;
        this.eventTriggers = eventTriggers;
        this.asyncCalls = asyncCalls;
        TableRowSorter<CustomTableModel> sorter = new TableRowSorter<>(tableModel);
        dbTable = new JTable(tableModel);
        dbTable.setRowSorter(sorter);
        addTableListener();
        this.setBackground(Constants.bgColor);
        this.setLayout(new BorderLayout());
        this.add(buildUIPanel(), BorderLayout.NORTH);
        this.add(new JScrollPane(dbTable), BorderLayout.CENTER);
        submitActionListener();
    } // constructor

    private void addTableListener() {
        dbTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) { return; }
            int viewRow = dbTable.getSelectedRow();
            if  (viewRow < 0) { return; }
            int rowIndex = dbTable.convertRowIndexToModel(viewRow);
            if (records == null || records.size() <= rowIndex) { return; }
            Record record = records.get(rowIndex);
            if (cachedImgs.containsKey(record.getID())) {
                albumArtLabel.setIcon(cachedImgs.get(record.getID()));
            } else {
                asyncCalls.asyncThumbnailCall(records.get(rowIndex).getThumbUrl(), albumArtLabel,
                        cachedImgs, record.getID());
            }
            if (cachedPricing.containsKey(record.getID())
                    && !cachedPricing.get(record.getID()).equals("Unavailable")) {
                pricingInfoLabel.setText(cachedPricing.get(record.getID()));
            } else {
                asyncCalls.asyncPricingCall(proxyClient, records.get(rowIndex).getID(), pricingInfoLabel,
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
        JButton addBtn = new JButton("Remove Album");
        addBtn.addActionListener(_ -> removeAlbumListener());
        temp.add(addBtn, c);
        return temp;
    } // buildUIPanel()

    private void removeAlbumListener() {
        int selectedIndex = dbTable.getSelectedRow();
        if (records == null || records.size() <= selectedIndex || selectedIndex < 0) {return;}
        int modelRow = dbTable.convertRowIndexToModel(selectedIndex);
        boolean deleted = dbAccess.deleteRecordEntry(records.get(modelRow).getID());
        if (deleted) {
            eventTriggers.updateStatsUI();
            records.remove(modelRow);
            tableModel.fireTableDataChanged();
            albumArtLabel.setIcon(Constants.defaultThumbNail);
            pricingInfoLabel.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Album could not be deleted",
                    "Error",
                    JOptionPane.OK_OPTION);
        }
    } // removeAlbumListener()

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
    } // build albumInfoDisplay()

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
        JPanel albumName =  getEntryField("Album Name: ", albumNameJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(albumName, c);
        JPanel yearField = getEntryField("Year: ", yearJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(yearField, c);
        JPanel catalogNo = getEntryField("Catalog No.", catNoJTF);
        c.gridy = gridyCounter++;
        searchEntryForm.add(catalogNo, c);
        JPanel ownedSelectorPanel = buildOwnedSelectorPanel();
        c.gridy = gridyCounter++;
        searchEntryForm.add(ownedSelectorPanel, c);
        JButton submit =  new JButton("Search");
        c.gridy = gridyCounter;
        searchEntryForm.add(submit, c);
        submit.addActionListener(_ -> submitActionListener());
        return searchEntryForm;
    } // buildSearchEntryForm()

    private JPanel buildOwnedSelectorPanel() {
        JPanel ownedSelectorPanel = new JPanel();
        ownedSelectorPanel.setBackground(Constants.bgColor);
        JRadioButton owned = new JRadioButton("Show Owned");
        owned.setActionCommand("true");
        owned.setBackground(Constants.bgColor);
        JRadioButton wanted = new JRadioButton("Show Wanted");
        wanted.setActionCommand("false");
        wanted.setBackground(Constants.bgColor);
        JRadioButton all =  new JRadioButton("Show All");
        all.setActionCommand("null");
        all.setBackground(Constants.bgColor);
        all.setSelected(true);
        ownedBtnGroup.add(owned);
        ownedBtnGroup.add(wanted);
        ownedBtnGroup.add(all);
        ownedSelectorPanel.add(owned);
        ownedSelectorPanel.add(wanted);
        ownedSelectorPanel.add(all);
        return ownedSelectorPanel;
    } // ownedRadioSelector

    private void submitActionListener() {
        records = searchDB();
        tableModel.fireTableDataChanged();
        albumArtLabel.setIcon(Constants.defaultThumbNail);
        pricingInfoLabel.setText("");
    }// submitActionListener()

    private ArrayList<Record> searchDB() {
        String artist = !artistNameJTF.getText().isBlank() ? artistNameJTF.getText() : null;
        String album = !albumNameJTF.getText().isBlank() ? albumNameJTF.getText() : null;
        String year = !yearJTF.getText().isBlank() ? yearJTF.getText() : null;
        String catNo = !catNoJTF.getText().isBlank() ? catNoJTF.getText() : null;
        return dbAccess.searchRecordEntries(artist, album, year, catNo, ownedBtnGroup.getSelection().getActionCommand());
    } // searchDB()

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


    private class CustomTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return records == null ? 0 : records.size();
        }

        @Override
        public int getColumnCount() {
            return Constants.dbColumnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex) {
                case 0 -> records.get(rowIndex).getCatNo();
                case 1 -> records.get(rowIndex).getArtistName();
                case 2 -> records.get(rowIndex).getAlbumName();
                case 3 -> records.get(rowIndex).getYear();
                case 4 -> records.get(rowIndex).getCountry();
                case 5 -> records.get(rowIndex).isOwned();
                default -> records.get(rowIndex).getCondition();
            };
        }

        @Override
        public String getColumnName(int column) {
            return Constants.dbColumnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 3) {return Integer.class;}
            else if (columnIndex == 5) {return Boolean.class;}
            return String.class;
        }

    } // CustomTableModel

} // DBSearchUI
