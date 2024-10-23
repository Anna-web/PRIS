package gui;

import consumption.ConsumptionCalculator;
import excel.ExcelWriter;
import reactors.Reactor;
import regions.Regions;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class ConsumptionCalculationsGUI extends JDialog {
    private final ConsumptionCalculator calculator;
    private final Regions regions;
    private JPanel contentPane;
    private JButton byCountryButton;
    private JButton byOperatorButton;
    private JButton byRegionButton;
    private JButton exportToExcelButton;
    private JTable resultTable;
    private Map<String, Map<Integer, Double>> currentCountryData;
    private Map<String, Map<Integer, Double>> currentOperatorData;
    private Map<String, Map<Integer, Double>> currentRegionData;

    public ConsumptionCalculationsGUI(Regions regions, Map<String, List<Reactor>> reactors) {
        this.regions = regions;
        calculator = new ConsumptionCalculator(reactors);
        initializeComponents();
        setupDialog();
        addListeners();
    }

    public static void main(String[] args) {
        Regions regions = new Regions();
        Map<String, List<Reactor>> reactors = new HashMap<>();
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void initializeComponents() {
        contentPane = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridBagLayout());
        byCountryButton = createStyledButton("COUNTRY");
        leftPanel.add(byCountryButton);
        byOperatorButton = createStyledButton("OPERATOR");
        leftPanel.add(byOperatorButton);
        byRegionButton = createStyledButton("REGION");
        leftPanel.add(byRegionButton);
        exportToExcelButton = createStyledButton("EXPORT TO EXCEL");
        exportToExcelButton.setEnabled(false);
        leftPanel.add(exportToExcelButton);
        contentPane.add(leftPanel, BorderLayout.NORTH);
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void setupDialog() {
        setModal(true);
        setTitle("Calculating consumption");
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        return button;
    }

    private void addListeners() {
        addCalculateListeners();
        exportToExcelButton.addActionListener(e -> {
            if (currentCountryData != null && currentOperatorData != null && currentRegionData != null) {
                ExcelWriter writer = new ExcelWriter();
                try {
                    writer.writeToExcel(currentCountryData, currentOperatorData, currentRegionData);
                    JOptionPane.showMessageDialog(this, "Data has been exported to Excel successfully.", "Export is done", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error while exporting to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No export data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void addCalculateListeners() {
        byCountryButton.addActionListener(e -> {
            currentCountryData = calculator.calculateConsumptionByCountries();
            currentCountryData = sortDataByCountry(currentCountryData);
            updateTable(currentCountryData, "Country");
            enableExportButtonIfReady();
        });

        byOperatorButton.addActionListener(e -> {
            currentOperatorData = calculator.calculateConsumptionByOperator();
            currentOperatorData = sortDataByCountry(currentOperatorData);
            updateTable(currentOperatorData, "Operator");
            enableExportButtonIfReady();
        });

        byRegionButton.addActionListener(e -> {
            currentRegionData = calculator.calculateConsumptionByRegions(regions);
            currentRegionData = sortDataByCountry(currentRegionData);
            updateTable(currentRegionData, "Region");
            enableExportButtonIfReady();
        });
    }

    private void enableExportButtonIfReady() {
        if (currentCountryData != null && currentOperatorData != null && currentRegionData != null) {
            exportToExcelButton.setEnabled(true);
        }
    }

    private Map<String, Map<Integer, Double>> sortDataByCountry(Map<String, Map<Integer, Double>> data) {
        Map<String, Map<Integer, Double>> sortedData = new TreeMap<>(Comparator.naturalOrder());
        sortedData.putAll(data);
        return sortedData;
    }

    private void updateTable(Map<String, Map<Integer, Double>> data, String header) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{header, "Consumption", "Year"});
        data.forEach((group, consumptionByYear) -> {
            consumptionByYear.keySet().stream().sorted().forEach(year -> {
                Double consumption = consumptionByYear.get(year);
                model.addRow(new Object[]{group, String.format("%1$.2f", consumption), year});
            });
        });
        resultTable.setModel(model);
    }
}