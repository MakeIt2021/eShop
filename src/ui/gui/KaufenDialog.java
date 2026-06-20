package ui.gui;

import entities.Rechnung;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog zur Anzeige der Rechnung
 * und zur Bestätigung eines Kaufs.
 */
public class KaufenDialog extends JDialog {

    // Buttons
    private JButton bestaetigenButton;
    private JButton abbrechenButton;

    public KaufenDialog(
            JFrame parent,
            Rechnung rechnung
    ) {

        super(parent, "Kaufen", true);

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // RECHNUNGSTEXT ERZEUGEN
        JTextArea rechnungText =
                new JTextArea();

        rechnungText.setEditable(false);
        rechnungText.setMargin(
                new Insets(
                        20,
                        50,
                        20,
                        50
                )
        );

        rechnungText.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        StringBuilder sb =
                new StringBuilder();

        sb.append("                 RECHNUNG\n");
        sb.append("=================================================\n\n");

        sb.append("Kunde: ")
                .append(rechnung.getKundeName())
                .append("\n");

        sb.append("Datum: ")
                .append(rechnung.getHeutigesDatum())
                .append("\n\n");

        sb.append("-------------------------------------------------\n");

        // Alle gekauften Artikel anzeigen
        for (Rechnung.GekaufterArtikel artikel
                : rechnung.gibAlleGekaufteArtikel()) {

            sb.append(
                    String.format(
                            "%-15s %3d x %8.2f € = %8.2f €\n",
                            artikel.bezeichnung(),
                            artikel.menge(),
                            artikel.preis(),
                            artikel.summe()
                    )
            );
        }

        sb.append("-------------------------------------------------\n");

        sb.append(
                String.format(
                        "%-25s %10.2f €\n",
                        "Summe:",
                        rechnung.getSumme()
                )
        );

        sb.append(
                String.format(
                        "%-25s %10.2f €\n",
                        "MwSt (19%):",
                        rechnung.getMwst()
                )
        );

        sb.append(
                String.format(
                        "%-25s %10.2f €\n",
                        "Gesamtpreis:",
                        rechnung.getGesamtPreis()
                )
        );

        rechnungText.setText(
                sb.toString()
        );

        add(
                new JScrollPane(rechnungText),
                BorderLayout.CENTER
        );

        // BUTTONS
        JPanel buttonPanel =
                new JPanel();

        bestaetigenButton =
                new JButton(
                        "✅ Kauf bestätigen"
                );

        abbrechenButton =
                new JButton(
                        "❌ Abbrechen"
                );

        buttonPanel.add(
                bestaetigenButton
        );

        buttonPanel.add(
                abbrechenButton
        );

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // ABBRECHEN

        abbrechenButton.addActionListener(
                e -> dispose()
        );
    }

    public JButton getBestaetigenButton() {
        return bestaetigenButton;
    }

    public JButton getAbbrechenButton() {
        return abbrechenButton;
    }
}