package eShop.client.ui.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Stellt das Registrierungs-Panel für neue Kunden dar.
 *
 * Über dieses Panel können neue Benutzer ihren
 * Namen, Benutzernamen und ihr Passwort eingeben,
 * um sich im eShop zu registrieren.
 */
public class RegistrierenPanel extends JPanel {

    /**
     * Eingabefeld für den Vor- und Nachnamen.
     */
    private JTextField nameField;

    /**
     * Eingabefeld für den Benutzernamen.
     */
    private JTextField benutzernameField;

    /**
     * Eingabefeld für das Passwort.
     */
    private JPasswordField passwortField;

    /**
     * Button zum Registrieren eines neuen Kunden.
     */
    private JButton registrierenButton;

    /**
     * Button zum Zurückkehren zur Login-Ansicht.
     */
    private JButton zurueckButton;

    /**
     * Erstellt das Registrierungs-Panel und
     * initialisiert alle grafischen Komponenten.
     */
    public RegistrierenPanel() {

        // Hauptlayout des Panels festlegen.
        setLayout(new BorderLayout());

        // Hintergrundfarbe setzen.
        setBackground(new Color(242, 246, 252));

        // ===========================
        // Titel
        // ===========================

        JLabel titleLabel =
                new JLabel("Kundenregistrierung");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        add(titleLabel, BorderLayout.NORTH);

        // ===========================
        // Formular
        // ===========================

        JPanel formPanel = new JPanel();

        // Komponenten werden untereinander angeordnet.
        formPanel.setLayout(new GridLayout(0, 1, 8, 8));

        formPanel.setBackground(new Color(242, 246, 252));

        // Rahmen des Formulars erstellen.
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.LIGHT_GRAY
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
        // Vor- und Nachname
        // ===========================

        formPanel.add(new JLabel("Vor- und Nachname:"));

        nameField = new JTextField(25);

        formPanel.add(nameField);

        // ===========================
        // Benutzername
        // ===========================

        formPanel.add(new JLabel("Benutzername:"));

        benutzernameField = new JTextField(25);

        formPanel.add(benutzernameField);

        // ===========================
        // Passwort
        // ===========================

        formPanel.add(new JLabel("Passwort:"));

        passwortField = new JPasswordField(25);

        formPanel.add(passwortField);

        // ===========================
        // Buttons
        // ===========================

        registrierenButton = new JButton("Registrieren");

        zurueckButton = new JButton("Zurück");

        formPanel.add(registrierenButton);

        formPanel.add(zurueckButton);

        // Formular dem Hauptpanel hinzufügen.
        add(formPanel, BorderLayout.CENTER);
    }

    /**
     * Gibt den Registrieren-Button zurück.
     *
     * Über diesen Button kann außerhalb der Klasse
     * ein ActionListener registriert werden.
     *
     * @return Registrieren-Button
     */
    public JButton getRegistrierenButton() {
        return registrierenButton;
    }

    /**
     * Gibt den Zurück-Button zurück.
     *
     * Mit diesem Button kann zur Login-Ansicht
     * zurückgekehrt werden.
     *
     * @return Zurück-Button
     */
    public JButton getZurueckButton() {
        return zurueckButton;
    }

    /**
     * Gibt den eingegebenen Vor- und Nachnamen zurück.
     *
     * @return Name des Benutzers
     */
    public String getName() {
        return nameField.getText();
    }

    /**
     * Gibt den eingegebenen Benutzernamen zurück.
     *
     * @return Benutzername
     */
    public String getBenutzername() {
        return benutzernameField.getText();
    }

    /**
     * Gibt das eingegebene Passwort zurück.
     *
     * Das Passwort wird als String zurückgegeben,
     * indem das Zeichenarray des Passwortfeldes
     * in einen String umgewandelt wird.
     *
     * @return Passwort
     */
    public String getPasswort() {
        return new String(passwortField.getPassword());
    }
}