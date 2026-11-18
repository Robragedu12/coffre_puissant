package auth;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class chiffrementMdp {

    // Clé fixe de 16 octets (128 bits) partagée entre le chiffrement et le déchiffrement
    // Uniquement utilisé pour rendre le mot de passe chiffré dans le fichier de gestion des mdps
    private static final byte[] CLE_FIXE = "CleSecrete123456".getBytes(StandardCharsets.UTF_8);
    private final Key key;

    public chiffrementMdp() {
        this.key = new SecretKeySpec(CLE_FIXE, "AES");
    }

    public String chiffrement(String mdp) throws InvalidKeyException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] messageChiffre = cipher.doFinal(mdp.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(messageChiffre);
    }

    public String dechiffrement(String mdpBase64) throws InvalidKeyException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] messageDecode = Base64.getDecoder().decode(mdpBase64);
        byte[] messageDechiffre = cipher.doFinal(messageDecode);

        return new String(messageDechiffre, StandardCharsets.UTF_8);
    }
}
