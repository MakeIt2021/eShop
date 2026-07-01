package ui.gui;

import domain.EShop;
import entities.Ereignis;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;

public class EreignisListeDialog extends JDialog {
    public EreignisListeDialog(Frame owner, EShop eShop) {
        super(owner, "Ereignisse", true);
        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        String[] titles = {"Tag", "Artikel", "Typ", "Menge", "Person"};

        DefaultTableModel model = new DefaultTableModel(titles, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> LocalDate.class;
                    case 3 -> Integer.class;
                    default -> String.class;
                };
            }
        };

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        filterPanel.add(new JLabel("Artikel:"));
        JTextField artikelFilterField = new JTextField(10);
        filterPanel.add(artikelFilterField);

        filterPanel.add(new JLabel("Person:"));
        JTextField personFilterField = new JTextField(10);
        filterPanel.add(personFilterField);

        filterPanel.add(new JLabel("Typ:"));
        String[] typen = {"Alle", "Einlagerung", "Auslagerung"};
        JComboBox<String> typComboBox = new JComboBox<>(typen);
        filterPanel.add(typComboBox);

        add(filterPanel, BorderLayout.NORTH);

        JTable table = new JTable(model);

        for (Ereignis e : eShop.gibEreignisListe()) {
            model.addRow(new Object[] {
                    e.getTag(),
                    e.getArtikel().getBezeichnung(),
                    e.getTyp(),
                    e.getMenge(),
                    e.getPerson()
            });
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        Runnable applyFilters = () -> {
            java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

            String artikelText = artikelFilterField.getText().trim();
            if (!artikelText.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(artikelText), 1));
            }

            String personText = personFilterField.getText().trim();
            if (!personText.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(personText), 4));
            }

            String selectedTyp = (String) typComboBox.getSelectedItem();
            if (selectedTyp != null && !selectedTyp.equals("Alle")) {
                filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedTyp) + "$", 2));
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null); // Если всё пусто — показываем все строки
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        artikelFilterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
        });

        personFilterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters.run(); }
        });

        typComboBox.addActionListener(e -> applyFilters.run());


        JButton closeButton = new JButton("Schließen");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
