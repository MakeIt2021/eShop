package eShop.client.ui.gui;

import eShop.common.entities.Ereignis;
import eShop.common.interfaces.EShopInterface;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.time.LocalDate;

/**
 * Stellt einen Dialog zur Anzeige und Filterung aller
 * gespeicherten Ereignisse des eShops dar.
 *
 * Die Ereignisse werden in einer Tabelle angezeigt.
 * Dabei werden folgende Informationen dargestellt:
 *
 * <ul>
 *     <li>Datum des Ereignisses,</li>
 *     <li>Bezeichnung des Artikels,</li>
 *     <li>Typ des Ereignisses,</li>
 *     <li>betroffene Menge,</li>
 *     <li>ausführende Person.</li>
 * </ul>
 *
 * Zusätzlich können die angezeigten Ereignisse nach Artikel,
 * Person und Ereignistyp gefiltert werden.
 *
 * Der Dialog ist modal. Das bedeutet, dass der Benutzer zuerst
 * diesen Dialog schließen muss, bevor er wieder mit dem
 * übergeordneten Fenster arbeiten kann.
 */
public class EreignisListeDialog extends JDialog {

    /**
     * Erzeugt einen neuen Dialog zur Anzeige der Ereignisliste.
     *
     * Der Konstruktor erstellt die Tabelle, lädt die Ereignisse
     * aus dem eShop und richtet die verschiedenen Filter ein.
     *
     * @param owner übergeordnetes Fenster, zu dem der Dialog gehört
     * @param eShop Schnittstelle zum Zugriff auf die eShop-Funktionen
     */
    public EreignisListeDialog(
            Frame owner,
            EShopInterface eShop
    ) {

        /*
         * Konstruktor der Oberklasse JDialog aufrufen.
         *
         * Der dritte Parameter true legt fest, dass der Dialog
         * modal ist.
         */
        super(owner, "Ereignisse", true);

        // Größe des Dialogfensters festlegen.
        setSize(700, 400);

        /*
         * Dialog relativ zum übergeordneten Fenster
         * auf dem Bildschirm positionieren.
         */
        setLocationRelativeTo(owner);

        /*
         * BorderLayout verwenden, um Filterbereich,
         * Tabelle und Schaltflächen anzuordnen.
         */
        setLayout(new BorderLayout());

        /*
         * Überschriften der Tabellenspalten definieren.
         */
        String[] titles = {
                "Tag",
                "Artikel",
                "Typ",
                "Menge",
                "Person"
        };

        /*
         * Tabellenmodell erzeugen.
         *
         * Das Modell enthält zu Beginn keine Datenzeilen.
         */
        DefaultTableModel model =
                new DefaultTableModel(titles, 0) {

                    /**
                     * Verhindert, dass Tabellenzellen vom Benutzer
                     * direkt bearbeitet werden können.
                     *
                     * @param row Zeilenindex
                     * @param column Spaltenindex
                     * @return immer {@code false}
                     */
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }

                    /**
                     * Gibt die Klasse der jeweiligen Tabellenspalte zurück.
                     *
                     * Dadurch kann der TableRowSorter die Werte
                     * entsprechend ihrem tatsächlichen Datentyp sortieren.
                     *
                     * @param columnIndex Index der Spalte
                     * @return Datentyp der Spalte
                     */
                    @Override
                    public Class<?> getColumnClass(
                            int columnIndex
                    ) {
                        return switch (columnIndex) {

                            // Die erste Spalte enthält ein Datum.
                            case 0 -> LocalDate.class;

                            // Die vierte Spalte enthält eine Ganzzahl.
                            case 3 -> Integer.class;

                            // Alle anderen Spalten enthalten Text.
                            default -> String.class;
                        };
                    }
                };

        /*
         * Panel für die Filterelemente erzeugen.
         *
         * Die Elemente werden von links nach rechts
         * mit Abständen angeordnet.
         */
        JPanel filterPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                5
                        )
                );

        // Beschriftung für den Artikelfilter hinzufügen.
        filterPanel.add(new JLabel("Artikel:"));

        /*
         * Texteingabefeld für die Filterung
         * nach Artikelbezeichnung.
         */
        JTextField artikelFilterField =
                new JTextField(10);

        filterPanel.add(artikelFilterField);

        // Beschriftung für den Personenfilter hinzufügen.
        filterPanel.add(new JLabel("Person:"));

        /*
         * Texteingabefeld für die Filterung
         * nach einer Person.
         */
        JTextField personFilterField =
                new JTextField(10);

        filterPanel.add(personFilterField);

        // Beschriftung für den Ereignistyp hinzufügen.
        filterPanel.add(new JLabel("Typ:"));

        /*
         * Mögliche Filterwerte für den Ereignistyp.
         *
         * "Alle" bedeutet, dass kein Typfilter angewendet wird.
         */
        String[] typen = {
                "Alle",
                "Einlagerung",
                "Auslagerung"
        };

        // Auswahlfeld für den Ereignistyp erzeugen.
        JComboBox<String> typComboBox =
                new JComboBox<>(typen);

        filterPanel.add(typComboBox);

        /*
         * Filterbereich im oberen Bereich
         * des Dialogs einfügen.
         */
        add(filterPanel, BorderLayout.NORTH);

        // Tabelle mit dem zuvor erzeugten Modell erstellen.
        JTable table = new JTable(model);

        /*
         * Alle Ereignisse aus dem eShop laden
         * und in das Tabellenmodell einfügen.
         */
        for (Ereignis e : eShop.gibEreignisListe()) {
            model.addRow(
                    new Object[]{
                            e.getTag(),
                            e.getArtikel().getBezeichnung(),
                            e.getTyp(),
                            e.getMenge(),
                            e.getPerson()
                    }
            );
        }

        /*
         * Sortierer für die Tabelle erzeugen.
         *
         * Dieser ermöglicht sowohl das Sortieren als auch
         * das Filtern der Tabellenzeilen.
         */
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);

        // Sortierer mit der Tabelle verbinden.
        table.setRowSorter(sorter);

        /*
         * Renderer erzeugen, damit Zahlen in der Mengenspalte
         * linksbündig angezeigt werden.
         */
        DefaultTableCellRenderer leftRenderer =
                new DefaultTableCellRenderer();

        leftRenderer.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        /*
         * Den Renderer auf die Spalte "Menge"
         * mit dem Index 3 anwenden.
         */
        table.getColumnModel()
                .getColumn(3)
                .setCellRenderer(leftRenderer);

        /*
         * Tabelle in einen Scrollbereich einfügen.
         *
         * Dadurch bleibt die Tabelle auch bei vielen Ereignissen
         * bedienbar.
         */
        JScrollPane scrollPane =
                new JScrollPane(table);

        // Tabelle im Zentrum des Dialogs anzeigen.
        add(scrollPane, BorderLayout.CENTER);

        /*
         * Gemeinsame Filterfunktion definieren.
         *
         * Diese Funktion liest alle Filterfelder aus und
         * kombiniert die aktiven Filter mit einer UND-Verknüpfung.
         */
        Runnable applyFilters = () -> {

            /*
             * Liste für alle aktuell aktiven Filter erzeugen.
             */
            java.util.List<RowFilter<Object, Object>> filters =
                    new java.util.ArrayList<>();

            /*
             * Inhalt des Artikelfilterfeldes lesen.
             *
             * trim() entfernt Leerzeichen am Anfang und Ende.
             */
            String artikelText =
                    artikelFilterField.getText().trim();

            /*
             * Nur dann einen Artikelfilter hinzufügen,
             * wenn das Feld nicht leer ist.
             */
            if (!artikelText.isEmpty()) {
                filters.add(
                        RowFilter.regexFilter(
                                "(?i)"
                                        + java.util.regex.Pattern.quote(
                                        artikelText
                                ),
                                1
                        )
                );
            }

            /*
             * Inhalt des Personenfilterfeldes lesen.
             */
            String personText =
                    personFilterField.getText().trim();

            /*
             * Nur dann einen Personenfilter hinzufügen,
             * wenn das Feld nicht leer ist.
             */
            if (!personText.isEmpty()) {
                filters.add(
                        RowFilter.regexFilter(
                                "(?i)"
                                        + java.util.regex.Pattern.quote(
                                        personText
                                ),
                                4
                        )
                );
            }

            /*
             * Aktuell ausgewählten Ereignistyp auslesen.
             */
            String selectedTyp =
                    (String) typComboBox.getSelectedItem();

            /*
             * Einen Typfilter nur anwenden, wenn ein konkreter Typ
             * und nicht "Alle" ausgewählt wurde.
             */
            if (selectedTyp != null
                    && !selectedTyp.equals("Alle")) {

                filters.add(
                        RowFilter.regexFilter(
                                "^"
                                        + java.util.regex.Pattern.quote(
                                        selectedTyp
                                )
                                        + "$",
                                2
                        )
                );
            }

            /*
             * Wenn keine Filter aktiv sind, werden alle
             * Tabellenzeilen angezeigt.
             */
            if (filters.isEmpty()) {
                sorter.setRowFilter(null);

            } else {
                /*
                 * Alle aktiven Filter werden mit einer
                 * UND-Verknüpfung kombiniert.
                 *
                 * Eine Zeile wird nur angezeigt, wenn sie
                 * alle Filterbedingungen erfüllt.
                 */
                sorter.setRowFilter(
                        RowFilter.andFilter(filters)
                );
            }
        };

        /*
         * DocumentListener für das Artikelfilterfeld.
         *
         * Der Filter wird bei jeder Änderung des Textes
         * sofort neu angewendet.
         */
        artikelFilterField
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            /**
                             * Wird ausgeführt, wenn Text eingefügt wird.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }

                            /**
                             * Wird ausgeführt, wenn Text entfernt wird.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }

                            /**
                             * Wird bei einer Änderung von Attributen
                             * im Dokument ausgeführt.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }
                        }
                );

        /*
         * DocumentListener für das Personenfilterfeld.
         *
         * Auch hier wird der Filter unmittelbar bei jeder
         * Änderung des Textes aktualisiert.
         */
        personFilterField
                .getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            /**
                             * Wird ausgeführt, wenn Text eingefügt wird.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }

                            /**
                             * Wird ausgeführt, wenn Text entfernt wird.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }

                            /**
                             * Wird bei einer Änderung von Attributen
                             * im Dokument ausgeführt.
                             *
                             * @param e Dokumentereignis
                             */
                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                applyFilters.run();
                            }
                        }
                );

        /*
         * Listener für die Auswahl des Ereignistyps.
         *
         * Sobald der Benutzer einen anderen Typ auswählt,
         * wird die Tabellenfilterung aktualisiert.
         */
        typComboBox.addActionListener(
                e -> applyFilters.run()
        );

        // Schaltfläche zum Schließen des Dialogs erzeugen.
        JButton closeButton =
                new JButton("Schließen");

        /*
         * Beim Anklicken wird der Dialog geschlossen
         * und seine Ressourcen werden freigegeben.
         */
        closeButton.addActionListener(
                e -> dispose()
        );

        /*
         * Panel für die Schaltfläche erzeugen.
         *
         * Die Schaltfläche wird rechtsbündig angezeigt.
         */
        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        buttonPanel.add(closeButton);

        /*
         * Schaltflächenbereich im unteren Teil
         * des Dialogs einfügen.
         */
        add(buttonPanel, BorderLayout.SOUTH);
    }
}