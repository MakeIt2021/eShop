package eShop.client.ui.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Stellt das Login-Panel des eShops dar.
 *
 * Über dieses Panel können sich Benutzer mit ihrem
 * Benutzernamen und Passwort anmelden oder zur
 * Registrierungsseite wechseln.
 */
public class LoginPanel extends JPanel {

    /**
     * Eingabefeld für den Benutzernamen.
     */
    private JTextField benutzernameField;

    /**
     * Eingabefeld für das Passwort.
     */
    private JPasswordField passwortField;

    /**
     * Button zum Anmelden.
     */
    private JButton loginButton;

    /**
     * Button zum Wechsel zur Registrierung.
     */
    private JButton registrierenButton;

    /**
     * Erstellt das Login-Panel und initialisiert
     * alle grafischen Komponenten.
     */
    public LoginPanel() {

        // Hauptlayout des Panels festlegen.
        setLayout(new GridBagLayout());

        // Hintergrundfarbe setzen.
        setBackground(new Color(119, 135, 145));

        // ===========================
        // Loginformular erstellen
        // ===========================

        JPanel formPanel = new JPanel();

        // Komponenten werden untereinander angeordnet.
        formPanel.setLayout(new GridLayout(0, 1, 8, 8));

        // Hintergrundfarbe des Formulars.
        formPanel.setBackground(new Color(242, 246, 252));

        // Feste Größe des Formulars.
        formPanel.setPreferredSize(new Dimension(400, 280));

        // Rahmen des Formulars erstellen.
        formPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
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

        JLabel titleLabel = new JLabel("Login");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        formPanel.add(titleLabel);

        // ===========================
        // Benutzername
        // ===========================

        formPanel.add(new JLabel("Benutzername:"));

        benutzernameField = new JTextField(20);

        formPanel.add(benutzernameField);

        // ===========================
        // Passwort
        // ===========================

        formPanel.add(new JLabel("Passwort:"));

        passwortField = new JPasswordField(20);

        formPanel.add(passwortField);

        // ===========================
        // Buttons
        // ===========================

        // Login-Button erstellen.
        loginButton = new JButton("Login");
        formPanel.add(loginButton);

        // Registrieren-Button erstellen.
        registrierenButton = new JButton("Registrieren");
        formPanel.add(registrierenButton);

        // Formular mittig im Panel platzieren.
        add(formPanel);
    }

    /**
     * Gibt den Login-Button zurück.
     *
     * Über diesen Button kann außerhalb der Klasse
     * ein ActionListener registriert werden.
     *
     * @return Login-Button
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * Gibt den Registrieren-Button zurück.
     *
     * @return Registrieren-Button
     */
    public JButton getRegistrierenButton() {
        return registrierenButton;
    }

    /**
     * Liefert den eingegebenen Benutzernamen.
     *
     * @return Benutzername als String
     */
    public String getBenutzername() {
        return benutzernameField.getText();
    }

    /**
     * Liefert das eingegebene Passwort.
     *
     * Das Passwort wird als String zurückgegeben,
     * indem das Zeichenarray des Passwortfeldes
     * in einen String umgewandelt wird.
     *
     * @return eingegebenes Passwort
     */
    public String getPasswort() {
        return new String(passwortField.getPassword());
    }
}