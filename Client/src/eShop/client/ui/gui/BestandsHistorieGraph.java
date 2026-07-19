package eShop.client.ui.gui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Stellt die Bestandshistorie eines Artikels als Liniendiagramm dar.
 *
 * Die Klasse erwartet eine Liste mit genau 30 Bestandswerten.
 * Jeder Wert repräsentiert den Bestand eines Artikels an einem
 * bestimmten Tag.
 *
 * Das Diagramm zeigt:
 *
 * <ul>
 *     <li>eine horizontale X-Achse für die Tage 1 bis 30,</li>
 *     <li>eine vertikale Y-Achse für die Bestandswerte,</li>
 *     <li>eine Linie zwischen den einzelnen Bestandswerten,</li>
 *     <li>Beschriftungen für Tage und Bestände.</li>
 * </ul>
 *
 * Sind keine oder weniger als 30 historische Werte vorhanden,
 * wird anstelle des Diagramms eine Hinweismeldung angezeigt.
 */
public class BestandsHistorieGraph extends JPanel {

    /**
     * Enthält die Bestandswerte der letzten 30 Tage.
     *
     * Der Eintrag an Index {@code 0} entspricht dem ersten Tag,
     * der Eintrag an Index {@code 29} dem dreißigsten Tag.
     */
    private ArrayList<Integer> bestandVon30Tagen;

    /**
     * Erzeugt eine neue grafische Darstellung der Bestandshistorie.
     *
     * @param bestandVon30Tagen Liste mit den Bestandswerten
     *                          der letzten 30 Tage
     */
    public BestandsHistorieGraph(ArrayList<Integer> bestandVon30Tagen) {
        /*
         * Die übergebene Liste wird im Attribut gespeichert,
         * damit sie beim Zeichnen verwendet werden kann.
         */
        this.bestandVon30Tagen = bestandVon30Tagen;
    }

    /**
     * Zeichnet die Bestandshistorie in das Panel.
     *
     * Die Methode wird automatisch von Swing aufgerufen,
     * wenn das Panel angezeigt oder neu gezeichnet werden muss.
     *
     * Sind keine ausreichenden historischen Daten vorhanden,
     * wird lediglich eine entsprechende Meldung ausgegeben.
     *
     * @param g Grafikobjekt, mit dem in das Panel gezeichnet wird
     */
    @Override
    protected void paintComponent(Graphics g) {
        /*
         * Prüfen, ob überhaupt historische Daten vorhanden sind.
         */
        if (bestandVon30Tagen == null || bestandVon30Tagen.isEmpty()) {
            g.drawString("Keine historischen Daten für diesen Artikel vorhanden.",20, 30);
            return;

        } else if (bestandVon30Tagen.size() < 30) {
            /*
             * Für das Diagramm werden genau 30 Tageswerte erwartet.
             */
            g.drawString("Nicht genug historischen Daten für diesen Artikel vorhanden, um ein Graph zu zeichnen.", 20, 30);
            return;
        }

        /*
         * Standarddarstellung des JPanel zeichnen.
         *
         * Dazu gehören insbesondere der Hintergrund
         * und weitere Komponenten des Panels.
         */
        super.paintComponent(g);

        /*
         * Aktuelle Breite und Höhe des Panels bestimmen.
         *
         * Dadurch passt sich das Diagramm automatisch
         * an die Größe des Fensters an.
         */
        int width = getWidth();
        int height = getHeight();

        /*
         * Abstand zwischen Diagramm und Panelrand.
         */
        int padding = 50;

        /*
         * Zeichenfarbe auf Schwarz setzen.
         */
        g.setColor(Color.BLACK);

        /*
         * Koordinaten der vertikalen Y-Achse festlegen.
         */
        int yAxisX = padding;
        int yAxisY1 = padding;
        int yAxisY2 = height - padding;

        /*
         * Y-Achse zeichnen.
         */
        g.drawLine(
                yAxisX,
                yAxisY1,
                yAxisX,
                yAxisY2
        );

        /*
         * Koordinaten der horizontalen X-Achse festlegen.
         */
        int xAxisX1 = padding;
        int xAxisX2 = width - padding;
        int xAxisY = height - padding;

        /*
         * X-Achse zeichnen.
         */
        g.drawLine(
                xAxisX1,
                xAxisY,
                xAxisX2,
                xAxisY
        );

        /*
         * Skalierungsfaktor für die X-Achse berechnen.
         *
         * Die verfügbare Breite wird gleichmäßig
         * auf 30 Tage verteilt.
         */
        double xScale =
                (double) (width - 2 * padding) / 30;

        /*
         * Größten Bestandswert ermitteln.
         *
         * Dieser Wert wird später benötigt,
         * um die Y-Achse passend zu skalieren.
         */
        int maxBestand = 0;

        for (int i = 0; i < 30; i++) {

            if (maxBestand < bestandVon30Tagen.get(i)) {
                maxBestand = bestandVon30Tagen.get(i);
            }
        }

        /*
         * Eine Division durch null verhindern.
         *
         * Wenn alle Bestände null sind, wird der maximale
         * Bestand auf 1 gesetzt.
         */
        if (maxBestand == 0) {
            maxBestand = 1;
        }

        /*
         * Skalierungsfaktor für die Y-Achse berechnen.
         *
         * Der größte Bestand soll innerhalb der verfügbaren
         * Diagrammhöhe dargestellt werden.
         */
        double yScale =
                (double) (height - 2 * padding)
                        / maxBestand;

        /*
         * Verbindungslinien zwischen den 30 Tageswerten zeichnen.
         *
         * Da immer zwei benachbarte Punkte verbunden werden,
         * entstehen insgesamt 29 Linien.
         */
        for (int i = 0; i < 29; i++) {

            /*
             * Koordinaten des ersten Punktes berechnen.
             */
            int x1 = padding + (int) (i * xScale);
            int y1 = height - padding - (int) (bestandVon30Tagen.get(i) * yScale);

            /*
             * X-Koordinate zur Kontrolle
             * in der Konsole ausgeben.
             *
             * Diese Ausgabe stammt aus dem ursprünglichen Code.
             */
            System.out.println(x1);

            /*
             * Koordinaten des folgenden Punktes berechnen.
             */
            int x2 = padding + (int) ((i + 1) * xScale);

            int y2 = height - padding - (int) (bestandVon30Tagen.get(i + 1) * yScale);

            /*
             * Beide Punkte miteinander verbinden.
             */
            g.drawLine(x1, y1, x2, y2);
        }

        /*
         * X-Skalierung erneut berechnen.
         *
         * Diese Zeile ist im ursprünglichen Code enthalten
         * und wird unverändert beibehalten.
         */
        xScale = (double) (width - 2 * padding) / 30;

        /*
         * Markierungen und Beschriftungen
         * für alle 30 Tage zeichnen.
         */
        for (int i = 0; i < 30; i++) {

            /*
             * Position der Tagesmarkierung berechnen.
             */
            int x = padding + (int) (i * xScale);

            int y = height - padding;

            /*
             * Kleine Markierung an der X-Achse zeichnen.
             */
            g.drawLine(
                    x,
                    y,
                    x,
                    y + 5
            );

            /*
             * Tagesnummer erzeugen.
             */
            String day = String.valueOf(i + 1);

            /*
             * Tagesnummer unterhalb der X-Achse anzeigen.
             */
            g.drawString(day, x - 4, y + 20);
        }

        /*
         * Liste für bereits gezeichnete Zahlen erzeugen.
         *
         * Laut Kommentar im ursprünglichen Code soll sie
         * möglicherweise später verhindern, dass Zahlen
         * zu nah beieinander dargestellt werden.
         *
         * Aktuell wird die Liste jedoch noch nicht verwendet.
         */
        ArrayList<Integer> drawn = new ArrayList<>();

        /*
         * Bestandswerte an der Y-Achse darstellen.
         */
        for (int i = 0; i < 30; i++) {
            /*
             * Aktuellen Bestandswert auslesen.
             */
            int value = bestandVon30Tagen.get(i);

            /*
             * Vertikale Position des Bestandswertes berechnen.
             */
            int y = height - padding - (int) (value * yScale);

            /*
             * Kleine Markierung an der Y-Achse zeichnen.
             */
            g.drawLine(padding - 5, y, padding, y);

            /*
             * Bestandswert links von der Y-Achse anzeigen.
             */
            g.drawString(String.valueOf(value), padding - 35, y + 5);
        }
    }
}