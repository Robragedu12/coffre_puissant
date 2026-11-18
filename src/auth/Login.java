package auth;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private String usersFilePath;
    private boolean succeeded = false;
    private String username;
    private Principal userPrincipal;
    private final chiffrementMdp chiffreur = new chiffrementMdp();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.usersFilePath = (String) options.get("user.json");
        if (this.usersFilePath == null) {
            this.usersFilePath = "users.json";
        }
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null || usersFilePath == null) {
            throw new LoginException("Configuration JAAS incomplète");
        }

        NameCallback nameCallback = new NameCallback("Identifiant : ");
        PasswordCallback passwordCallback = new PasswordCallback("Mot de passe : ", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
            username = nameCallback.getName();
            char[] password = passwordCallback.getPassword();

            if (username == null || password == null) {
                throw new LoginException("Identifiants manquants");
            }
            // Lecture du JSON sans bibliothèque externe
            Map<String, String> users = parseSimpleJson(usersFilePath);

            String storedPasswordChiffre = users.get(username);
            String inputPassword = new String(password);
            passwordCallback.clearPassword();

            if (storedPasswordChiffre == null) {
                throw new LoginException("Identifiant ou mot de passe incorrect");
            }

            // On déchiffre le mot de passe stocké dans le JSON
            String storedPasswordClair = chiffreur.dechiffrement(storedPasswordChiffre);

            // Comparaison sécurisée en temps constant des deux mots de passe en clair
            if (MessageDigest.isEqual(
                    storedPasswordClair.getBytes(StandardCharsets.UTF_8),
                    inputPassword.getBytes(StandardCharsets.UTF_8))) {
                succeeded = true;
                return true;
            } else {
                throw new LoginException("Identifiant ou mot de passe incorrect");
            }

        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Erreur lors de la lecture du fichier : " + e.getMessage());
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parseSimpleJson(String filePath) throws IOException {
        String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();

        // Extrait les paires "clé" : "valeur" du JSON plat
        Matcher matcher = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"").matcher(content);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    @Override
    public boolean commit() {
        if (!succeeded) return false;
        userPrincipal = () -> username;
        subject.getPrincipals().add(userPrincipal);
        return true;
    }

    @Override
    public boolean abort() {
        if (!succeeded) return false;
        logout();
        return true;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().remove(userPrincipal);
        succeeded = false;
        username = null;
        userPrincipal = null;
        return true;
    }
}
