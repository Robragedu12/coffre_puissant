package reseau;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import javax.crypto.* ;
import javax.crypto.spec.SecretKeySpec;
import java.security.* ;
import java.util.Base64;

public class Serveur {
    public static void lancerServeur(int port) {
        try {
            String ip = AdresseServeurAuto.getIPv4("Intel");
            System.out.println("Adresse serveur : " + ip);
            InetAddress adr = InetAddress.getByName(ip);

            try (ServerSocket s = new ServerSocket(port, 10, adr)) {
                System.out.println("Serveur en attente de connexion...");

                try (Socket c = s.accept()) {
                    System.out.println("Client connecté");

                    BufferedReader entree = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter sortie = new PrintWriter(new OutputStreamWriter(c.getOutputStream(), StandardCharsets.UTF_8), true);

                    String cleClient;
                    String messageCrypte;
                    String messageClient;

                    while ((cleClient = entree.readLine()) != null) {
                        messageCrypte = entree.readLine();

                        if (cleClient == null || messageCrypte == null) {
                            break;
                        }

                        System.out.println("Clé reçue : " + cleClient);
                        System.out.println("Message chiffré reçu : " + messageCrypte);
                        byte[] decodedKeyBytes = Base64.getDecoder().decode(cleClient);
                        byte[] decodedResultBytes = Base64.getDecoder().decode(messageCrypte);

                        Cipher cipher = Cipher.getInstance("AES") ;
                        SecretKeySpec cleClientOk = new SecretKeySpec(decodedKeyBytes, "AES");
                        cipher.init(Cipher.DECRYPT_MODE, cleClientOk) ;


                        byte[] original = cipher.doFinal(decodedResultBytes) ;

                        messageClient = new String(original);
                        if ("bye".equalsIgnoreCase(messageClient.trim())) {
                            sortie.println("fin de transmission");
                            break;
                        } else {
                            sortie.println("Message bien reçu : " + messageClient);
                        }
                    }
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException |
                         BadPaddingException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Connexion client fermée. Le serveur s'arrête.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
