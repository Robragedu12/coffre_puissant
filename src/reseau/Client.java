package reseau;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.crypto.*;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    private static final Map<String, Key> trousseauLocal = new HashMap<>();

    public static void demarrerSessionInteractive(int port, String IP) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez votre login : ");
        String login = scanner.nextLine().trim();

        boolean continuer = true;
        while (continuer) {
            System.out.println("\n=== COFFRE-FORT DE " + login.toUpperCase() + " ===");
            System.out.println("1. Envoyer un fichier");
            System.out.println("2. Lister mes fichiers (list)");
            System.out.println("3. Lire et déchiffrer un fichier");
            System.out.println("4. Quitter");
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    System.out.print("Chemin du fichier à envoyer (ex: mon_test.txt) : ");
                    String cheminFichier = scanner.nextLine().trim();
                    envoyerFichier(port, IP, login, cheminFichier);
                    break;

                case "2":
                    listerFichiers(port, IP, login);
                    break;

                case "3":
                    System.out.print("ID du fichier à lire : ");
                    String idStockage = scanner.nextLine().trim();
                    lireFichier(port, IP, login, idStockage);
                    break;

                case "4":
                    fermerServeur(port, IP, login);
                    continuer = false;
                    System.out.println("Déconnexion.");
                    break;

                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private static void envoyerFichier(int port, String IP, String login, String cheminFichier) {
        try {
            Path path = Paths.get(cheminFichier);
            if (!Files.exists(path)) {
                System.err.println("Erreur : fichier introuvable.");
                return;
            }

            byte[] data = Files.readAllBytes(path);
            String nomOriginal = path.getFileName().toString();

            KeyGenerator kg = KeyGenerator.getInstance("AES");
            Key key = kg.generateKey();

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String fichierCrypteBase64 = Base64.getEncoder().encodeToString(cipher.doFinal(data));

            try (Socket s = new Socket(InetAddress.getByName(IP), port);
                 BufferedReader entree = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter sortie = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

                sortie.println("ENVOYER");
                sortie.println(login);
                sortie.println(nomOriginal);
                sortie.println(fichierCrypteBase64);

                String idStockage = entree.readLine();
                trousseauLocal.put(idStockage, key);
                System.out.println("Succès ! Fichier stocké sous l'ID : " + idStockage);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi : " + e.getMessage());
        }
    }

    private static void listerFichiers(int port, String IP, String login) {
        try (Socket s = new Socket(InetAddress.getByName(IP), port);
             BufferedReader entree = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter sortie = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            sortie.println("LIST");
            sortie.println(login);

            int nbFichiers = Integer.parseInt(entree.readLine());
            System.out.println("\n--- VOS FICHIERS SUR LE SERVEUR (" + nbFichiers + ") ---");

            if (nbFichiers == 0) {
                System.out.println("Aucun fichier enregistré.");
            } else {
                for (int i = 0; i < nbFichiers; i++) {
                    String ligne = entree.readLine();
                    String[] parts = ligne.split(";");
                    if (parts.length == 3) {
                        String nom = parts[0];
                        String id = parts[1];
                        String taille = parts[2];
                        String statutCle = trousseauLocal.containsKey(id) ? "[Clé disponible]" : "[Clé absente]";
                        System.out.println("- " + nom + " (" + taille + " octets) | ID: " + id + " " + statutCle);
                    }
                }
            }
            System.out.println("------------------------------------------");

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de la liste : " + e.getMessage());
        }
    }

    private static void lireFichier(int port, String IP, String login, String idStockage) {
        Key key = trousseauLocal.get(idStockage);
        if (key == null) {
            System.err.println("Erreur : vous ne possédez pas la clé locale pour cet ID.");
            return;
        }

        try (Socket s = new Socket(InetAddress.getByName(IP), port);
             BufferedReader entree = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter sortie = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {

            sortie.println("LIRE");
            sortie.println(login);
            sortie.println(idStockage);

            String statut = entree.readLine();
            if (!"OK".equals(statut)) {
                System.err.println("Erreur du serveur : " + statut);
                return;
            }

            String fichierCrypteBase64 = entree.readLine();
            byte[] donneesChiffrees = Base64.getDecoder().decode(fichierCrypteBase64);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] donneesDechiffrees = cipher.doFinal(donneesChiffrees);

            System.out.println("\n--- CONTENU DÉCHIFFRÉ ---");
            System.out.println(new String(donneesDechiffrees, StandardCharsets.UTF_8));
            System.out.println("-------------------------");

        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture : " + e.getMessage());
        }
    }

    private static void fermerServeur(int port, String IP, String login) {
        try (Socket s = new Socket(InetAddress.getByName(IP), port);
             PrintWriter sortie = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true)) {
            sortie.println("QUITTER");
            sortie.println(login);
        } catch (IOException ignored) {}
    }
}
