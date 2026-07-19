package eShop.client.ui.gui;

import eShop.common.entities.Rechnung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Stellt einen Dialog zur Anzeige einer Rechnung
 * und zur Bestätigung eines Kaufs dar.
 *
 * Der Dialog zeigt den Kundennamen, das Datum,
 * die ausgewählten Artikel sowie die Preisberechnung an.
 * Der Benutzer kann den Kauf bestätigen oder abbrechen.
 */
public class KaufenDialog extends JDialog {

    /**
     * Button zur Bestätigung des Kaufs.
     */
    private JButton bestaetigenButton;

    /**
     * Button zum Abbrechen und Schließen des Dialogs.
     */
    private JButton abbrechenButton;

    /**
     * Tabellenmodell für die Anzeige der gekauften Artikel.
     */
    private DefaultTableModel tabelleModel;

    /**
     * Tabelle mit den ausgewählten Artikeln.
     */
    private JTable ausgewaehlteArtikel;

    /**
     * Erstellt einen neuen Rechnungsdialog.
     *
     * @param parent übergeordnetes Hauptfenster
     * @param rechnung Rechnung, deren Daten angezeigt werden
     */
    public KaufenDialog(JFrame parent, Rechnung rechnung) {

        // Modalen Dialog mit dem Titel "Rechnung" erzeugen.
        super(parent, "Rechnung", true);

        // Größe und Position des Dialogs festlegen.
        setSize(600, 500);
        setLocationRelativeTo(parent);

        // BorderLayout als Hauptlayout verwenden.
        setLayout(new BorderLayout());

        // Oberen Bereich für Titel, Kundendaten und Datum erstellen.
        JPanel topBar = new JPanel(new BorderLayout());

        // Titel der Rechnung erstellen.
        JLabel titleLabel = new JLabel("Rechnung");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topBar.add(titleLabel, BorderLayout.NORTH);

        // Panel für Kundenname und Rechnungsdatum.
        JPanel clientAndDatePanel = new JPanel(new FlowLayout());

        JLabel clientLabel = new JLabel("Kunde: " + rechnung.getKundeName());

        JLabel dateLabel = new JLabel("Datum: " + rechnung.getHeutigesDatum());

        clientAndDatePanel.add(clientLabel);
        clientAndDatePanel.add(dateLabel);

        topBar.add(clientAndDatePanel, BorderLayout.CENTER);

        // Trennlinie unterhalb der Kundendaten.
        JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);

        separator1.setPreferredSize(new Dimension(10, 15));

        topBar.add(separator1, BorderLayout.SOUTH);

        // Oberen Bereich dem Dialog hinzufügen.
        add(topBar, BorderLayout.NORTH);

        // Hauptbereich für Tabelle und Preisangaben.
        JPanel artikelInfoPanel = new JPanel(new BorderLayout());

        // Spaltennamen der Artikeltabelle.
        String[] spalten = {
                "Bezeichnung",
                "Menge",
                "Preis"
        };

        // Tabellenmodell erstellen.
        // Die Zellen sollen nicht bearbeitet werden können.
        tabelleModel = new DefaultTableModel(spalten, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tabelle mit dem Tabellenmodell erzeugen.
        ausgewaehlteArtikel = new JTable(tabelleModel);

        // Tabellenkopf gestalten.
        ausgewaehlteArtikel
                .getTableHeader()
                .setBackground(new Color(70, 130, 180));

        ausgewaehlteArtikel
                .getTableHeader()
                .setForeground(Color.WHITE);

        ausgewaehlteArtikel
                .getTableHeader()
                .setFont(new Font("Arial", Font.BOLD, 14));

        // Tabelle scrollbar machen.
        JScrollPane scrollPane =
                new JScrollPane(ausgewaehlteArtikel);

        artikelInfoPanel.add(scrollPane, BorderLayout.CENTER);

        // Trennlinie zwischen Artikeltabelle und Preisübersicht.
        JSeparator separator2 =
                new JSeparator(SwingConstants.HORIZONTAL);

        separator2.setPreferredSize(new Dimension(10, 15));

        artikelInfoPanel.add(separator2, BorderLayout.SOUTH);

        // Panel für Summe, Mehrwertsteuer und Gesamtpreis.
        JPanel summeInfo = new JPanel(new GridLayout(3, 1));

        JLabel summe =
                new JLabel("Summe: " + rechnung.getSumme() + "€");

        JLabel mwst =
                new JLabel("MwSt (19%): " + rechnung.getMwst() + "€");

        JLabel gesamtpreis =
                new JLabel("Gesamtpreis: " + rechnung.getGesamtPreis() + "€");

        summeInfo.add(summe);
        summeInfo.add(mwst);
        summeInfo.add(gesamtpreis);

        // Preisübersicht dem Artikelbereich hinzufügen.
        artikelInfoPanel.add(summeInfo, BorderLayout.SOUTH);

        // Artikelbereich dem Dialog hinzufügen.
        add(artikelInfoPanel, BorderLayout.CENTER);

        // Bereich für die Aktionsbuttons.
        JPanel buttonPanel = new JPanel();

        // Button zur Kaufbestätigung.
        bestaetigenButton = new JButton("✅ Kauf bestätigen");

        // Button zum Abbrechen.
        abbrechenButton = new JButton("❌ Abbrechen");

        buttonPanel.add(bestaetigenButton);
        buttonPanel.add(abbrechenButton);

        // Buttonbereich am unteren Rand anzeigen.
        add(buttonPanel, BorderLayout.SOUTH);

        // Dialog beim Klick auf "Abbrechen" schließen.
        abbrechenButton.addActionListener(e -> dispose());
    }

    /**
     * Zeigt alle in der Rechnung enthaltenen Artikel
     * in der Artikeltabelle an.
     *
     * Vor dem Einfügen werden alle bereits vorhandenen
     * Tabellenzeilen entfernt.
     *
     * @param rechnung Rechnung mit den gekauften Artikeln
     */
    public void zeigeAusgewaehlteArtikel(Rechnung rechnung) {
        // Vorhandene Tabelleninhalte entfernen.
        tabelleModel.setRowCount(0);

        // Alle gekauften Artikel in die Tabelle einfügen.
        for (Rechnung.GekaufterArtikel artikel
                : rechnung.gibAlleGekaufteArtikel()) {

            tabelleModel.addRow(
                    new Object[]{
                            artikel.bezeichnung(),
                            artikel.menge()
                                    + " * "
                                    + artikel.preis(),
                            artikel.summe()
                    }
            );
        }
    }

    /**
     * Gibt den Button zur Bestätigung des Kaufs zurück.
     *
     * Über diesen Button kann außerhalb der Klasse
     * ein ActionListener registriert werden.
     *
     * @return Bestätigungsbutton
     */
    public JButton getBestaetigenButton() {
        return bestaetigenButton;
    }
}