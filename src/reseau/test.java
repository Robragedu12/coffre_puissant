package reseau;

import auth.Controleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class test {

    public static void main(String[] args) throws IOException {
        int port = 40981;
        System.setProperty("java.security.auth.login.config", "jaas.config");
        String ip = "10.157.12.211";

        // Lancer le serveur dans un thread séparé
        Thread serveurThread = new Thread(() -> {
            System.out.println("===== LANCEMENT SERVEUR =====");
            Serveur.lancerServeur(port);
        });
        serveurThread.setDaemon(true);
        serveurThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Lancer le client
        System.out.println("===== LANCEMENT CLIENT =====");
        BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

        // authentification
        try {
            LoginContext lc = new LoginContext("MonAppAuth", new Controleur());
            lc.login();

            Subject subject = lc.getSubject();
            System.out.println("Authentification réussie !");
            System.out.println("Utilisateur connecté : " + subject.getPrincipals());
            Client.lancerClient(port, ip);
            lc.logout();
        }catch (LoginException e) {
            throw new RuntimeException(e);
        }

    }
}
