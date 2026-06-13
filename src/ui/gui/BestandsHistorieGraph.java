package ui.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BestandsHistorieGraph extends JPanel {
    private ArrayList<Integer> bestandVon30Tagen;

    public BestandsHistorieGraph(ArrayList<Integer> bestandVon30Tagen) {
        this.bestandVon30Tagen = bestandVon30Tagen;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        int padding = 50;

        g.setColor(Color.BLACK);

        int yAxisX = padding;
        int yAxisY1 = padding;
        int yAxisY2 = height - padding;
        g.drawLine(yAxisX, yAxisY1, yAxisX, yAxisY2);

        int xAxisX1 = padding;
        int xAxisX2 = width - padding;
        int xAxisY = height - padding;
        g.drawLine(xAxisX1, xAxisY, xAxisX2, xAxisY);

        double xScale = (double) (width - 2 * padding) / 30;

        int maxBestand = 0;
        for (int i = 0; i < 30; i++) {
            if (maxBestand < bestandVon30Tagen.get(i)) {
                maxBestand = bestandVon30Tagen.get(i);
            }
        }

        if (maxBestand == 0)
            maxBestand = 1;

        double yScale = (double) (height - 2 * padding) / maxBestand;


        for (int i = 0; i < 29; i++) {
            int x1 = padding + (int) (i * xScale);
            int y1 = height - padding - (int) (bestandVon30Tagen.get(i) * yScale);
            System.out.println(x1);

            int x2 = padding + (int) ((i + 1) * xScale);
            int y2 = height - padding - (int) (bestandVon30Tagen.get(i + 1) * yScale);

            g.drawLine(x1, y1, x2, y2);
        }

        xScale = (double) (width - 2 * padding) / 30;

        for (int i = 0; i < 30; i++) {
            int x = padding + (int) (i * xScale);
            int y = height - padding;

            g.drawLine(x, y, x, y + 5);
            String day = String.valueOf(i + 1);

            g.drawString(day, x - 4, y + 20);
        }

        ArrayList<Integer> drawn = new ArrayList<>(); // falls wir die Zahlen nicht so in der Nähe voneinander haben möchten

        for (int i = 0; i < 30; i++) {
            int value = bestandVon30Tagen.get(i);
            int y = height - padding - (int) (value * yScale);

            g.drawLine(padding - 5, y, padding, y);

            g.drawString(String.valueOf(value), padding - 35, y + 5);

        }
    }
}
