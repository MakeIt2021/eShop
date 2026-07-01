package ui.gui;

import domain.EShop;
import domain.exceptions.MassengutartikelmengeNichtTeilbarException;
import domain.exceptions.MengeWenigerAlsPackungGroesseException;
import domain.exceptions.UngueltigeMengeException;
import domain.exceptions.UngueltigerPreisException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ArtikelVeraendernDialog extends JDialog {
    private final EShop eShop;
    private final Runnable refreshTableCallback;
    private final int artikelID;

    private JTextField artikelBezeichnungField;
    private JTextField artikelBestandField;
    private JTextField artikelPreisField;
    private JTextField neuePackungsGroesseField;

    public ArtikelVeraendernDialog(Frame owner, EShop eShop, Runnable refreshTableCallback, int artikelID) {
        super(owner, "Artikel verändern", true);
        this.eShop = eShop;
        this.refreshTableCallback = refreshTableCallback;
        this.artikelID = artikelID;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel inputsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputsPanel.add(new JLabel("Neue Bezeichnung:"));
        artikelBezeichnungField = new JTextField(10);
        inputsPanel.add(artikelBezeichnungField);

        inputsPanel.add(new JLabel("Neuer Bestand:"));
        artikelBestandField = new JTextField(10);
        inputsPanel.add(artikelBestandField);

        inputsPanel.add(new JLabel("Neuer Preis:"));
        artikelPreisField = new JTextField(10);
        inputsPanel.add(artikelPreisField);

        inputsPanel.add(new JLabel("Neue Packungsgröße:"));
        neuePackungsGroesseField = new JTextField(10);
        inputsPanel.add(neuePackungsGroesseField);

        add(inputsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        JButton deleteButton = new JButton("Artikel Löschen");
        deleteButton.setForeground(Color.RED);

        JButton cancelButton = new JButton("Abbrechen");
        JButton saveButton = new JButton("Speichern");

        buttonsPanel.add(deleteButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        add(buttonsPanel, BorderLayout.SOUTH);


        artikelBezeichnungField.setText(eShop.getArtikelName(artikelID));
        artikelBestandField.setText(String.valueOf(eShop.getBestand(artikelID)));
        artikelPreisField.setText(String.valueOf(eShop.gibPreis(artikelID)));
        neuePackungsGroesseField.setText(String.valueOf(eShop.getPackungGroesse(artikelID)));

        if (!eShop.istMassengutartikel(artikelID)) {
            neuePackungsGroesseField.setEnabled(false);
        }

        cancelButton.addActionListener(e -> dispose());

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Möchten Sie diesen Artikel wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                eShop.artikelVernichten(artikelID);
                refreshTableCallback.run();
                JOptionPane.showMessageDialog(this, "Artikel erfolgreich gelöscht!");
                dispose();
            }
        });

        saveButton.addActionListener(e -> {
            try {
                String neueBezeicnnung = artikelBezeichnungField.getText().trim();
                BigDecimal neuerPreis = new BigDecimal(artikelPreisField.getText().trim());
                int neuerBestand = Integer.parseInt(artikelBestandField.getText().trim());
                int neuePackungsGroesse = Integer.parseInt(neuePackungsGroesseField.getText().trim());

                if (neueBezeicnnung.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Die Bezeichnung darf nicht leer sein!");
                    return;
                }

                eShop.bezeichnungVeraendern(artikelID, neueBezeicnnung);
                eShop.preisVeraendern(artikelID, neuerPreis);
                eShop.bestandVeraendern(artikelID, neuerBestand, eShop.aktuellerBenutzer().getBenutzerErkennung());

                if (eShop.istMassengutartikel(artikelID)) {
                    eShop.packungGroesseVeraendern(artikelID, neuePackungsGroesse);
                }

                refreshTableCallback.run();
                dispose();
            } catch (MassengutartikelmengeNichtTeilbarException | MengeWenigerAlsPackungGroesseException | UngueltigeMengeException | UngueltigerPreisException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}