package eShop.client.ui.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Stellt das Login-Panel für Mitarbeiter dar.
 *
 * Über dieses Panel können sich Mitarbeiter mit ihrer
 * Benutzerkennung und ihrem Passwort anmelden.
 */
public class mitarbeiterLoginPanel extends JPanel {

    /**
     * Eingabefeld für den Benutzernamen.
     */
    private JTextField benutzerName;

    /**
     * Eingabefeld für das Passwort.
     */
    private JPasswordField benutzerpasswort;

    /**
     * Button zum Anmelden.
     */
    private JButton loginButton;

    /**
     * Erstellt das Login-Panel für Mitarbeiter
     * und initialisiert alle grafischen Komponenten.
     */
    public mitarbeiterLoginPanel() {

        // Hauptlayout des Panels festlegen.
        setLayout(new GridLayout(3, 2, 10, 10));

        // Hintergrundfarbe setzen.
        setBackground(Color.WHITE);

        // ===========================
        // Formular erstellen
        // ===========================

        JPanel formPanel = new JPanel();

        formPanel.setLayout(new GridLayout(3, 2, 10, 10));

        formPanel.setBackground(Color.WHITE);

        // Größe des Formulars festlegen.
        formPanel.setPreferredSize(new Dimension(400, 400));

        // Rahmen des Formulars erstellen.
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.getColor("yellow")
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        // ===========================
        // Titel
        // ===========================

        JLabel titelLabel =
                new JLabel("Mitarbeiter Login!");

        titelLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        titelLabel.setForeground(Color.WHITE);
        titelLabel.setBackground(Color.WHITE);

        titelLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        formPanel.add(titelLabel);

        // ===========================
        // Benutzername
        // ===========================

        formPanel.add(new JLabel("Benutzername:"));

        benutzerName = new JTextField();

        formPanel.add(benutzerName);

        // ===========================
        // Passwort
        // ===========================

        formPanel.add(new JLabel("Passwort:"));

        benutzerpasswort = new JPasswordField();

        formPanel.add(benutzerpasswort);

        // ===========================
        // Login-Button
        // ===========================

        loginButton = new JButton("Login");

        formPanel.add(loginButton);

        // Formular dem Panel hinzufügen.
        add(formPanel);
    }
}