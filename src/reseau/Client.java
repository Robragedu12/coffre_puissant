package reseau;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.crypto.* ;
import java.security.* ;
import java.util.Base64;

public class Client {
    public static void lancerClient(int port, String IP) {

        byte[] data ;
        byte[] result ;
        byte[] original ;

        try {
            InetAddress adr = InetAddress.getByName(IP);
            System.out.println("Connexion au serveur " + IP + ":" + port + "...");

            try (Socket s = new Socket(adr, port);
                 BufferedReader entree = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter sortie = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in)))
            {
                KeyGenerator kg = KeyGenerator.getInstance("AES") ;
                Key key = kg.generateKey() ;
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, key);


                System.out.println("Connecté :");
                String messageTexte;

                while ((messageTexte = clavier.readLine()) != null) {

                    data= messageTexte.getBytes();
                    String keyAsString = Base64.getEncoder().encodeToString(key.getEncoded());
                    sortie.println(keyAsString);

                    result = cipher.doFinal(data);
                    String resultAsstring = Base64.getEncoder().encodeToString(result);
                    sortie.println(resultAsstring);

                    String reponseServeur = entree.readLine();

                    if (reponseServeur == null) {
                        System.out.println("Le serveur a fermé la connexion.");
                        break;
                    }

                    System.out.println("Serveur :  " + reponseServeur);

                    if ("bye".equalsIgnoreCase(messageTexte.trim())) {
                        break;
                    }

                }
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
