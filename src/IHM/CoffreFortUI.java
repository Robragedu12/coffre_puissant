package IHM;
import auth.Controleur;
import auth.SignIn;

import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;

public class CoffreFortUI extends JFrame {

    public CoffreFortUI() {

        // Fenêtre principale
        setTitle("Coffre fort numérique");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titre = new JLabel("Coffre fort numérique");

        gbc.gridy = 0;
        gbc.insets = new Insets(50, 10, 70, 10);
        panel.add(titre);

        // Bouton connexion
        JButton btnConnexion = new JButton("Connexion");
        btnConnexion.setPreferredSize(new Dimension(220, 55));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 15, 10);
        panel.add(btnConnexion, gbc);

        // Bouton création de compte
        JButton btnInscription = new JButton("Crée un compte");
        btnInscription.setPreferredSize(new Dimension(220, 55));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 10, 10);
        panel.add(btnInscription, gbc);

        // Actions
        btnConnexion.addActionListener(e -> {
            try {
                ouvrirConnexion();
            } catch (LoginException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnInscription.addActionListener(e -> {
                try {
                    creeConnexion();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            ex.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

        });


        add(panel);
        setVisible(true);
    }

    private void ouvrirConnexion() throws LoginException {

        JTextField champId = new JTextField(15);
        JPasswordField champMdp = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Identifiant :"));
        panel.add(champId);

        panel.add(new JLabel("Mot de passe :"));
        panel.add(champMdp);

        int resultat = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Connexion",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultat == JOptionPane.OK_OPTION) {

            String id = champId.getText();
            char[] mdp = champMdp.getPassword();

            try {
                System.setProperty(
                        "java.security.auth.login.config",
                        "jaas.config"
                );

                CallbackHandler handler = callbacks -> {
                    for (Callback callback : callbacks) {

                        if (callback instanceof NameCallback nc) {
                            nc.setName(id);

                        } else if (callback instanceof PasswordCallback pc) {
                            pc.setPassword(mdp);

                        } else {
                            throw new UnsupportedCallbackException(callback);
                        }
                    }
                };

                LoginContext lc = new LoginContext("MonAppAuth", handler);

                lc.login();

                JOptionPane.showMessageDialog(this, "Connexion réussie !");

            } catch (LoginException ex) {

                JOptionPane.showMessageDialog(this, ex.getMessage());

            } finally {
                java.util.Arrays.fill(mdp, '\0');
            }
        }
    }

    private void creeConnexion(){
        JTextField champId = new JTextField(15);
        JPasswordField champMdp = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Identifiant :"));
        panel.add(champId);

        panel.add(new JLabel("Mot de passe :"));
        panel.add(champMdp);

        int resultat = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Création du compte",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        try{
            String id = champId.getText();
            String mdp = champMdp.getText();
            SignIn signIn = new SignIn();
            signIn.ajoutUtilisateur(mdp, id);

            JOptionPane.showMessageDialog(this, "Création du compte réussi !");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CoffreFortUI());
    }
}
