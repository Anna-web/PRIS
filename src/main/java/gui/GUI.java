package gui;

import reactors.Reactor;
import readers.DBReader;
import regions.Regions;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class GUI extends JFrame {
    private JButton importButton;
    private JButton goCalculateButton;
    private JButton exitButton;
    private JPanel mainPanel;
    private JTree reactorsTree;
    private Regions regions;
    private Map<String, List<Reactor>> reactors;

    public GUI() throws URISyntaxException {
        setLookAndFeel();
        initializeComponents();
        setupFrame();
        createUIComponents();
        addListeners();
        setVisible(true);
    }

    public static void main(String[] args) throws URISyntaxException {
        new GUI();
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        importButton = new JButton("SELECT DATABASE");
        goCalculateButton = new JButton("CALCULATE CONSUMPTION");
        exitButton = new JButton("EXIT");
        mainPanel = new JPanel(new BorderLayout());
        reactorsTree = new JTree(new DefaultMutableTreeNode("Reactors"));
        reactorsTree.setEnabled(false);
        goCalculateButton.setEnabled(false);
        exitButton.setEnabled(false);
    }

    private void setupFrame() {
        setTitle("Лабораторная работа №4");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
    }

    private void createUIComponents() {
        JScrollPane scrollPane = new JScrollPane(reactorsTree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(importButton);
        buttonPanel.add(goCalculateButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    private void addListeners() {
        importButton.addActionListener(e -> showFileChooser());
        reactorsTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showReactorDetails();
                }
            }
        });
        goCalculateButton.addActionListener(e -> showCalculatorDialog());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Database file", "db");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("./"));
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getName().toLowerCase().endsWith(".db")) {
                fillTree(file);
            } else {
                showErrorDialog("Chose the correct format: .db");
            }
        }
    }

    private void showReactorDetails() {
        TreePath selectionPath = reactorsTree.getSelectionPath();
        if (selectionPath == null) return;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        if (selectedNode.getUserObject() instanceof Reactor reactor) {
            String title = "Reactor " + reactor.getName();
            String message = reactor.getFullDescription();
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea textArea = new JTextArea(message);
            panel.add(textArea, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(null, panel, title, JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void showCalculatorDialog() {
        ConsumptionCalculationsGUI dialog = new ConsumptionCalculationsGUI(regions, reactors);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void fillTree(File file) {
        try {
            reactors = new TreeMap<>(DBReader.importReactors(file));
            regions = DBReader.importRegions(file);
            populateTree();
            reactorsTree.setEnabled(true);
            goCalculateButton.setEnabled(true);
            exitButton.setEnabled(true);
        } catch (SQLException e) {
            showErrorDialog("Error while importing database");
        }
    }

    private void populateTree() {
        DefaultTreeModel treeModel = (DefaultTreeModel) reactorsTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Reactors");
        Map<String, DefaultMutableTreeNode> countryNodes = new HashMap<>();

        for (Map.Entry<String, List<Reactor>> entry : reactors.entrySet()) {
            String country = entry.getValue().get(0).getCountry();
            DefaultMutableTreeNode countryNode = countryNodes.get(country);
            if (countryNode == null) {
                countryNode = new DefaultMutableTreeNode(country);
                countryNodes.put(country, countryNode);
                root.add(countryNode);
            }
            for (Reactor reactor : entry.getValue()) {
                DefaultMutableTreeNode reactorNode = new DefaultMutableTreeNode(reactor);
                countryNode.add(reactorNode);
            }
        }
        List<DefaultMutableTreeNode> sortedCountryNodes = new ArrayList<>(countryNodes.values());
        sortedCountryNodes.sort(Comparator.comparing(DefaultMutableTreeNode::toString));
        sortedCountryNodes.forEach(root::add);
        treeModel.setRoot(root);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}