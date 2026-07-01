package ui.gui;
import javax.swing.*;
import java.awt.*;

public class mitarbeiterLoginPanel extends JPanel {
    private JTextField benutzerName;
    private JPasswordField benutzerpasswort;
    private JButton loginButton;
// login constructor
    public mitarbeiterLoginPanel() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBackground(new Color(255, 255, 255));

        JPanel formPanel =  new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(255, 255, 255));

        formPanel.setPreferredSize(new Dimension(400,400));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.getColor("yellow")),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        // label design
        JLabel titelLabel = new JLabel("Mitarbeiter Login!");
        titelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titelLabel.setForeground(Color.white);
        titelLabel.setBackground(Color.white);
        titelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titelLabel);

    //  label fur benutzerName
        formPanel.add(new JLabel("Benutzername: "));
        benutzerName = new JTextField();
        formPanel.add(benutzerName);
    // pass lebel
        formPanel.add(new JLabel("Password: "));
        benutzerpasswort = new JPasswordField();
        formPanel.add(benutzerpasswort);
        // login label button;
        loginButton = new JButton("Login");
        formPanel.add(loginButton);
    }

// getter to read the information
    public String getBenutzerName() {
        return benutzerName.getText();
    }
    public String getBenutzerpasswort() {
        return new String(benutzerpasswort.getPassword());
    }
    public JButton getLoginButton() {
        return loginButton;
    }

    public void setBenutzerName(String benutzerErkennung) {
        benutzerName.setText(benutzerErkennung);
    }
}




