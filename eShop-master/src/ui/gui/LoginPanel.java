package ui.gui;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField benutzernameField;
    private JPasswordField passwortField;
    private JButton loginButton;
    private JButton registrierenButton;

    public LoginPanel() {

        // Hauptlayout
        setLayout(new GridBagLayout());
        setBackground(new Color(119, 135, 145));

        // Formularpanel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 8, 8));
        formPanel.setBackground(new Color(242, 246, 252));

        // Feste Größe des Loginformulars
        formPanel.setPreferredSize(new Dimension(400, 280));
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

        // Titel
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 28)
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel);

        // Benutzername
        formPanel.add(new JLabel("Benutzername:"));
        benutzernameField = new JTextField(20);
        formPanel.add(benutzernameField);

        // Passwort
        formPanel.add(new JLabel("Passwort:"));
        passwortField = new JPasswordField(20);
        formPanel.add(passwortField);

        // Login Button
        loginButton = new JButton("Login");
        formPanel.add(loginButton);

        // Registrieren Button
        registrierenButton = new JButton("Registrieren");
        formPanel.add(registrierenButton);

        // Formular zentrieren
        add(formPanel);
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getRegistrierenButton() {
        return registrierenButton;
    }

    public String getBenutzername() {
        return benutzernameField.getText();
    }

    public String getPasswort() {
        return new String(passwortField.getPassword());
    }
}
