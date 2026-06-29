package ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;


public class mitarbeiterMainPanel extends JPanel {
    private JLabel mitarbeiterLabel;
    private JTextField sucheField;
    private JButton logoutButton;

    private JButton artikelHinzufuegenButton;
    private JButton artikelVeraendernButton;
    private JButton ereignisseButton;
    private JButton historieButton;
    private JButton mitarbeiterRegButton;

    //mitarbeiter artikel tabelle "bestand"
    private JTable artikelTabelle;
    private DefaultTableModel artikelTabelleModel;

    public mitarbeiterMainPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(255, 255, 255));

        //titel label
        JLabel eShopLabel = new JLabel("eShop - Mitarbeiter");
        eShopLabel.setFont(new Font("Arial", Font.BOLD, 26));
        eShopLabel.setHorizontalAlignment(SwingConstants.CENTER);
        northPanel.add(eShopLabel, BorderLayout.NORTH);

        // such bereich panel
        JPanel suchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        suchPanel.setBackground(new Color(255, 255, 255));
        suchPanel.add(new JLabel("Artikel suchen:"));
        sucheField = new JTextField(15);
        suchPanel.add(sucheField);

        northPanel.add(suchPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        String[] spalten = {"ID", "Bezeichnung", "Bestand", "Preis"};
        artikelTabelleModel = new DefaultTableModel(spalten,0) {
            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };
        artikelTabelle = new JTable(artikelTabelleModel);
        JScrollPane sp = new JScrollPane(artikelTabelle);
        add(sp, BorderLayout.CENTER);

        //south bereich
        JPanel southPanel = new  JPanel(new GridLayout(2, 1));
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonBar.setBackground(new Color(242, 246, 252));

        artikelHinzufuegenButton = new JButton("Artikel Hinzufügen");
        artikelVeraendernButton = new JButton("Artikel verändern");
        historieButton = new JButton("Bestandhistorie ansehen");

        ereignisseButton = new JButton("Ereignisse ansehen");
        mitarbeiterRegButton = new JButton("Mitarbeiter registrieren");

        buttonBar.add(artikelHinzufuegenButton);
        buttonBar.add(artikelVeraendernButton);
        buttonBar.add(historieButton);

        JLabel separator = new JLabel("|");
        separator.setForeground(Color.LIGHT_GRAY); // Делаем её ненавязчиво серой
        separator.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // Отступы слева и справа
        buttonBar.add(separator);

        buttonBar.add(ereignisseButton);
        buttonBar.add(mitarbeiterRegButton);

        southPanel.add(buttonBar);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(230, 235, 245));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        mitarbeiterLabel = new JLabel("Mitarbeiter: nicht angemeldet");
        statusBar.add(mitarbeiterLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");
        statusBar.add(logoutButton, BorderLayout.EAST);

        southPanel.add(statusBar);

        add(southPanel, BorderLayout.SOUTH);

    }

    public DefaultTableModel getArtikelTabelleModel() {
        return artikelTabelleModel;
    }
    public JTable getArtikelTabelle() { return artikelTabelle; }
    public JButton getLogoutButton() {
        return logoutButton;
    }
    public JTextField getSucheField() { return sucheField; }
    public JLabel getMitarbeiterLabel() { return mitarbeiterLabel; }

    public JButton getArtikelHinzufuegenButton() { return artikelHinzufuegenButton; }
    public JButton getArtikelVeraendernButton() { return artikelVeraendernButton; }
    public JButton getHistorieButton() { return historieButton; }

    public JButton getEreignisseButton() { return ereignisseButton; }
    public JButton getMitarbeiterRegButton() { return mitarbeiterRegButton; }

    public void setMitarbeiterName(String name){
        mitarbeiterLabel.setText("Mitarbeiter: " + name);
    }



    public void filtereArtikel(String suchbegriff) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(artikelTabelleModel);
        artikelTabelle.setRowSorter(sorter);
        if (suchbegriff == null || suchbegriff.isBlank()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + suchbegriff,
                            1 // Spalte "Bezeichnung"
                    )
            );
        }
    }
}
