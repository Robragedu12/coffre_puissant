package reseau;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) {
        int port = 12345;
        String fichierExemple = "mon_test.txt";

        try {
            Files.writeString(Paths.get(fichierExemple), "Message secret : le serveur ne possède aucune clé.", StandardCharsets.UTF_8);

            String ipServeur = AdresseServeurAuto.getIPv4("Intel");
            if (ipServeur == null) ipServeur = "127.0.0.1";

            Thread threadServeur = new Thread(() -> Serveur.lancerServeur(port));
            threadServeur.start();
            Thread.sleep(500);

            Client.demarrerSessionInteractive(port, ipServeur);

            threadServeur.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
