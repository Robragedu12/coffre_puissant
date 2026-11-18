package reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class test {

    public static void main(String[] args) throws IOException {
        int port = 40981;
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

        // authentification (non)
        System.out.print("Saisir le mdp : ");
        String messageTexte = clavier.readLine();
        if (Objects.equals(messageTexte, "mdp")){
            Client.lancerClient(port, ip);
        } else {
            System.out.println("Pas le bon mdp");
        }
    }
}
