package auth;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SignIn {

    private final Path fichierJson = Path.of("users.json");
    private final chiffrementMdp chiffreur = new chiffrementMdp();

    public void ajoutUtilisateur(String mdp, String login) throws Exception {
        Map<String, String> users = lireUtilisateurs();
        String mdpChiffre = chiffreur.chiffrement(mdp);
        users.put(login, mdpChiffre);
        ecrireUtilisateurs(users);
    }

    private Map<String, String> lireUtilisateurs() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        if (!Files.exists(fichierJson)) {
            return map;
        }

        String content = Files.readString(fichierJson, StandardCharsets.UTF_8);
        Matcher matcher = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"").matcher(content);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

    private void ecrireUtilisateurs(Map<String, String> users) throws Exception {
        String entrees = users.entrySet().stream()
                .map(e -> "  \"" + e.getKey() + "\": \"" + e.getValue() + "\"")
                .collect(Collectors.joining(",\n"));

        String jsonFinal = "{\n" + entrees + "\n}";
        Files.writeString(fichierJson, jsonFinal, StandardCharsets.UTF_8);
    }

    public List<String> obtenirId() throws Exception {
        return new ArrayList<>(lireUtilisateurs().keySet());
    }
}
