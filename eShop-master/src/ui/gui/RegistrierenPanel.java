package ui.gui;

import javax.swing.*;
import java.awt.*;

public class RegistrierenPanel extends JPanel {
    // Eingabefeld für Vor- und Nachname
    private JTextField nameField;

    // Eingabefeld für Benutzername
    private JTextField benutzernameField;

    // Passwortfeld
    private JPasswordField passwortField;

    // Button zum Registrieren
    private JButton registrierenButton;

    // Button zum Zurückkehren zum Login
    private JButton zurueckButton;

    public RegistrierenPanel() {

        // Hauptlayout des Panels
        setLayout(new BorderLayout());

        // Hintergrundfarbe des Panels
        setBackground(new Color(242, 246, 252));

        // Überschrift
        JLabel titleLabel = new JLabel("Kundenregistrierung");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Formularbereich
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 8, 8));
        formPanel.setBackground(new Color(242, 246, 252));

        // Rahmen um das Formular
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        );
        // Vor- und Nachname
        formPanel.add(new JLabel("Vor- und Nachname:"));
        nameField = new JTextField(25);
        formPanel.add(nameField);

        // Benutzername
        formPanel.add(new JLabel("Benutzername:"));
        benutzernameField = new JTextField(25);
        formPanel.add(benutzernameField);

        // Passwort
        formPanel.add(new JLabel("Passwort:"));
        passwortField = new JPasswordField(25);
        formPanel.add(passwortField);

        // Buttons
        registrierenButton = new JButton("Registrieren");
        zurueckButton = new JButton("Zurück");
        formPanel.add(registrierenButton);
        formPanel.add(zurueckButton);

        // Formular zum Hauptpanel hinzufügen
        add(formPanel, BorderLayout.CENTER);
    }

    // Liefert den Registrieren-Button zurück
    public JButton getRegistrierenButton() {
        return registrierenButton;
    }

    // Liefert den Zurück-Button zurück
    public JButton getZurueckButton() {
        return zurueckButton;
    }

    // Liefert den eingegebenen Namen
    public String getName() {
        return nameField.getText();
    }

    // Liefert den eingegebenen Benutzernamen
    public String getBenutzername() {
        return benutzernameField.getText();
    }

    // Liefert das eingegebene Passwort
    public String getPasswort() {
        return new String(
                passwortField.getPassword()
        );
    }
}