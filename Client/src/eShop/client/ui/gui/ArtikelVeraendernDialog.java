package eShop.client.ui.gui;

import eShop.common.exceptions.*;
import eShop.common.interfaces.EShopInterface;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Stellt einen modalen Dialog zum Bearbeiten oder Löschen
 * eines bereits vorhandenen Artikels dar.
 *
 * Im Dialog können folgende Eigenschaften eines Artikels
 * verändert werden:
 *
 * <ul>
 *     <li>Bezeichnung,</li>
 *     <li>Bestand,</li>
 *     <li>Preis,</li>
 *     <li>Packungsgröße bei einem Massengutartikel.</li>
 * </ul>
 *
 * Zusätzlich kann der ausgewählte Artikel vollständig
 * aus dem eShop gelöscht werden.
 *
 * Nach einer erfolgreichen Änderung oder Löschung wird
 * über den {@code refreshTableCallback} die Artikeltabelle
 * im übergeordneten Fenster aktualisiert.
 */
public class ArtikelVeraendernDialog extends JDialog {
    /**
     * Schnittstelle zum Zugriff auf die Funktionen des eShops.
     *
     * Über diese Referenz werden die Artikeldaten geladen,
     * verändert oder gelöscht.
     */
    private final EShopInterface eShop;

    /**
     * Rückruffunktion zur Aktualisierung der Artikeltabelle.
     *
     * Die Funktion wird nach einer erfolgreichen Änderung
     * oder Löschung eines Artikels ausgeführt.
     */
    private final Runnable refreshTableCallback;
    /**
     * Eindeutige Identifikationsnummer des Artikels,
     * der in diesem Dialog bearbeitet wird.
     */
    private final int artikelID;

    /**
     * Eingabefeld für die neue Artikelbezeichnung.
     */
    private JTextField artikelBezeichnungField;

    /**
     * Eingabefeld für den neuen Artikelbestand.
     */
    private JTextField artikelBestandField;

    /**
     * Eingabefeld für den neuen Artikelpreis.
     */
    private JTextField artikelPreisField;

    /**
     * Eingabefeld für die neue Packungsgröße.
     * Dieses Feld ist nur bei einem Massengutartikel aktiviert.
     */
    private JTextField neuePackungsGroesseField;;


    /**
     * Erzeugt einen neuen Dialog zum Bearbeiten eines Artikels.
     * Beim Öffnen des Dialogs werden die aktuellen Artikeldaten
     * aus dem eShop geladen und in die entsprechenden
     * Eingabefelder eingetragen.
     *
     * @param owner übergeordnetes Fenster des Dialogs
     * @param eShop Schnittstelle zum Zugriff auf den eShop
     * @param refreshTableCallback Rückruffunktion zur Aktualisierung
     *                             der Artikeltabelle
     * @param artikelID ID des Artikels, der bearbeitet werden soll
     */
    public ArtikelVeraendernDialog(Frame owner, EShopInterface eShop, Runnable refreshTableCallback, int artikelID) {
        /*
         * Konstruktor der Oberklasse JDialog aufrufen.
         *
         * Der Wert true legt fest, dass der Dialog modal ist.
         * Der Benutzer muss den Dialog also zuerst schließen,
         * bevor er wieder mit dem Hauptfenster arbeiten kann.
         */
        super(owner, "Artikel verändern", true);

        /*
         * Übergebene Werte in den Attributen speichern.
         */
        this.eShop = eShop;
        this.refreshTableCallback = refreshTableCallback;
        this.artikelID = artikelID;

        /*
         * Größe des Dialogfensters festlegen.
         */
        setSize(400, 300);

        /*
         * Dialog relativ zum übergeordneten Fenster positionieren.
         */
        setLocationRelativeTo(owner);

        /*
         * BorderLayout für die Anordnung der Komponenten verwenden.
         */
        setLayout(new BorderLayout());

        /*
         * Panel für die Eingabefelder erzeugen.
         *
         * Das GridLayout ordnet die Komponenten in vier Zeilen
         * und zwei Spalten an.
         */
        JPanel inputsPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        /*
         * Innenabstand zwischen Panelrand und Eingabefeldern setzen.
         */
        inputsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /*
         * Beschriftung und Eingabefeld für die neue
         * Artikelbezeichnung hinzufügen.
         */
        inputsPanel.add(new JLabel("Neue Bezeichnung:"));
        artikelBezeichnungField = new JTextField(10);
        inputsPanel.add(artikelBezeichnungField);

        /*
         * Beschriftung und Eingabefeld für den neuen Bestand
         * hinzufügen.
         */
        inputsPanel.add(new JLabel("Neuer Bestand:"));
        artikelBestandField = new JTextField(10);
        inputsPanel.add(artikelBestandField);

        /*
         * Beschriftung und Eingabefeld für den neuen Preis
         * hinzufügen.
         */
        inputsPanel.add(new JLabel("Neuer Preis:"));
        artikelPreisField = new JTextField(10);
        inputsPanel.add(artikelPreisField);

        /*
         * Beschriftung und Eingabefeld für die neue Packungsgröße
         * hinzufügen.
         */
        inputsPanel.add(new JLabel("Neue Packungsgröße:"));
        neuePackungsGroesseField = new JTextField(10);
        inputsPanel.add(neuePackungsGroesseField);

        /*
         * Eingabebereich im Zentrum des Dialogs einfügen.
         */
        add(inputsPanel, BorderLayout.CENTER);

        /*
         * Panel für die Schaltflächen erzeugen.
         *
         * Die Schaltflächen werden rechtsbündig angeordnet.
         */
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        /*
         * Schaltfläche zum Löschen des Artikels erzeugen.
         */
        JButton deleteButton = new JButton("Artikel Löschen");
        /*
         * Schriftfarbe der Löschschaltfläche auf Rot setzen,
         * um die kritische Aktion hervorzuheben.
         */
        deleteButton.setForeground(Color.RED);

        /*
         * Schaltfläche zum Abbrechen erzeugen.
         */
        JButton cancelButton = new JButton("Abbrechen");
        /*
         * Schaltfläche zum Speichern erzeugen.
         */
        JButton saveButton = new JButton("Speichern");

        /*
         * Schaltflächen zum Panel hinzufügen.
         */
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        /*
         * Schaltflächenbereich im unteren Teil des Dialogs einfügen.
         */
        add(buttonsPanel, BorderLayout.SOUTH);

        /*
         * Aktuelle Bezeichnung des Artikels aus dem eShop laden
         * und in das Eingabefeld eintragen.
         */
        artikelBezeichnungField.setText(eShop.gibArtikelName(artikelID));

        /*
         * Aktuellen Bestand laden und als Text anzeigen.
         */
        artikelBestandField.setText(String.valueOf(eShop.gibBestand(artikelID)));

        /*
         * Aktuellen Preis laden und als Text anzeigen.
         */
        artikelPreisField.setText(String.valueOf(eShop.gibPreis(artikelID)));

        /*
         * Aktuelle Packungsgröße laden und als Text anzeigen.
         */
        neuePackungsGroesseField.setText(String.valueOf(eShop.gibPackungGroesse(artikelID)));

        /*
         * Bei einem normalen Artikel darf die Packungsgröße
         * nicht verändert werden.
         *
         * Deshalb wird das entsprechende Eingabefeld deaktiviert.
         */
        if (!eShop.istMassengutartikel(artikelID)) {
            neuePackungsGroesseField.setEnabled(false);
        }

        /*
         * Dialog schließen, wenn auf "Abbrechen" geklickt wird.
         *
         * Es werden dabei keine Änderungen gespeichert.
         */
        cancelButton.addActionListener(e -> dispose());

        /*
         * Aktion für die Löschschaltfläche festlegen.
         */
        deleteButton.addActionListener(e -> {

            /*
             * Sicherheitsabfrage anzeigen, bevor der Artikel
             * tatsächlich gelöscht wird.
             */
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Möchten Sie diesen Artikel wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            /*
             * Artikel nur löschen, wenn der Benutzer
             * die Aktion ausdrücklich bestätigt.
             */
            if (confirm == JOptionPane.YES_OPTION) {
                /*
                 * Artikel über die eShop-Schnittstelle löschen.
                 */
                eShop.artikelVernichten(artikelID);

                /*
                 * Artikeltabelle im übergeordneten Fenster
                 * aktualisieren.
                 */
                refreshTableCallback.run();

                /*
                 * Erfolgsmeldung anzeigen.
                 */
                JOptionPane.showMessageDialog(this, "Artikel erfolgreich gelöscht!");

                /*
                 * Dialog nach erfolgreicher Löschung schließen.
                 */
                dispose();
            }
        });

        /*
         * Aktion für die Speichern-Schaltfläche festlegen.
         */
        saveButton.addActionListener(e -> {
            try {

                /*
                 * Neue Bezeichnung aus dem Eingabefeld lesen.
                 *
                 * trim() entfernt Leerzeichen am Anfang und Ende.
                 */
                String neueBezeicnnung = artikelBezeichnungField.getText().trim();

                /*
                 * Preis aus dem Textfeld lesen und in BigDecimal
                 * umwandeln.
                 *
                 * BigDecimal eignet sich für Geldbeträge,
                 * da Dezimalwerte exakt dargestellt werden.
                 */
                BigDecimal neuerPreis = new BigDecimal(artikelPreisField.getText().trim());

                /*
                 * Bestand aus dem Textfeld lesen und
                 * in eine Ganzzahl umwandeln.
                 */
                int neuerBestand = Integer.parseInt(artikelBestandField.getText().trim());

                /*
                 * Packungsgröße aus dem Textfeld lesen und
                 * in eine Ganzzahl umwandeln.
                 */
                int neuePackungsGroesse = Integer.parseInt(neuePackungsGroesseField.getText().trim());

                /*
                 * Prüfen, ob eine gültige Bezeichnung eingegeben
                 * wurde.
                 */
                if (neueBezeicnnung.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Die Bezeichnung darf nicht leer sein!");

                    /*
                     * Speichervorgang abbrechen.
                     */
                    return;
                }

                /*
                 * Artikelbezeichnung ändern.
                 */
                eShop.bezeichnungVeraendern(artikelID, neueBezeicnnung);

                /*
                 * Artikelpreis ändern.
                 */
                eShop.preisVeraendern(artikelID, neuerPreis);

                /*
                 * Artikelbestand ändern.
                 *
                 * Zusätzlich wird die Benutzerkennung des aktuell
                 * angemeldeten Benutzers übergeben, damit die
                 * Bestandsänderung als Ereignis gespeichert werden kann.
                 */
                eShop.bestandVeraendern(artikelID, neuerBestand, eShop.aktuellerBenutzer().getBenutzerErkennung());

                /*
                 * Packungsgröße nur bei einem Massengutartikel ändern.
                 */
                if (eShop.istMassengutartikel(artikelID)) {
                    eShop.packungGroesseVeraendern(artikelID, neuePackungsGroesse);
                }

                /*
                 * Artikeltabelle nach erfolgreicher Änderung
                 * aktualisieren.
                 */
                refreshTableCallback.run();

                /*
                 * Dialog schließen.
                 */
                dispose();
            } catch (MassengutartikelmengeNichtTeilbarException | MengeWenigerAlsPackungGroesseException | UngueltigeMengeException | UngueltigerPreisException |
                     ArtikelExistiertBereitsException ex) {
                /*
                 * Fachliche Fehler aus dem eShop abfangen
                 * und dem Benutzer anzeigen.
                 */
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}