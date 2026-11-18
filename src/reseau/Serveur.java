package reseau;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class Serveur {
    public static void lancerServeur(int port) {
        try {
            String ip = AdresseServeurAuto.getIPv4("Intel");
            if (ip == null) ip = "127.0.0.1";
            InetAddress adr = InetAddress.getByName(ip);

            try (ServerSocket s = new ServerSocket(port, 10, adr)) {
                System.out.println("[Serveur] Démarré sur " + ip + ":" + port);

                while (true) {
                    try (Socket c = s.accept()) {
                        BufferedReader entree = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
                        PrintWriter sortie = new PrintWriter(new OutputStreamWriter(c.getOutputStream(), StandardCharsets.UTF_8), true);

                        String commande = entree.readLine();
                        String login = entree.readLine();

                        if ("ENVOYER".equals(commande)) {
                            String nomFichier = entree.readLine();
                            String fichierCrypteBase64 = entree.readLine();

                            byte[] donneesChiffrees = Base64.getDecoder().decode(fichierCrypteBase64);
                            String idStockage = Stockage.sauvegarderFichier(login, nomFichier, donneesChiffrees);

                            System.out.println("[Serveur] Fichier chiffré stocké pour '" + login + "' -> " + idStockage);
                            sortie.println(idStockage);

                        } else if ("LIRE".equals(commande)) {
                            String idStockage = entree.readLine();

                            try {
                                byte[] donneesChiffrees = Stockage.lireFichier(login, idStockage);
                                String fichierCrypteBase64 = Base64.getEncoder().encodeToString(donneesChiffrees);

                                sortie.println("OK");
                                sortie.println(fichierCrypteBase64);
                            } catch (IOException e) {
                                sortie.println("ERREUR : " + e.getMessage());
                            }
                        } else if ("LIST".equals(commande)) {
                            List<String> fichiers = Stockage.listerFichiers(login);
                            sortie.println(fichiers.size());
                            for (String ligne : fichiers) {
                                sortie.println(ligne);
                            }
                        } else if ("QUITTER".equals(commande)) {
                            System.out.println("[Serveur] Arrêt.");
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
