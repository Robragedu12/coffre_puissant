package reseau;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Stockage {

    private static final Path DOSSIER_RACINE = Paths.get("stockage_serveur", "coffres");

    public static String sauvegarderFichier(String utilisateur, String nomOriginal, byte[] donneesChiffrees) throws IOException {
        Path dossierUser = DOSSIER_RACINE.resolve(utilisateur);
        Files.createDirectories(dossierUser);
        String idStockage = UUID.randomUUID().toString() + ".enc";
        Path cheminFichier = dossierUser.resolve(idStockage);
        Files.write(cheminFichier, donneesChiffrees);

        Path metaFile = dossierUser.resolve("metadata.txt");
        String ligneMeta = nomOriginal + ";" + idStockage + ";" + donneesChiffrees.length + "\n";
        Files.writeString(metaFile, ligneMeta, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        return idStockage;
    }

    public static byte[] lireFichier(String utilisateur, String idStockage) throws IOException {
        Path cheminFichier = DOSSIER_RACINE.resolve(utilisateur).resolve(idStockage);
        if (!Files.exists(cheminFichier)) {
            throw new IOException("Fichier introuvable sur le serveur.");
        }
        return Files.readAllBytes(cheminFichier);
    }
    
    public static List<String> listerFichiers(String utilisateur) throws IOException {
        Path metaFile = DOSSIER_RACINE.resolve(utilisateur).resolve("metadata.txt");
        if (!Files.exists(metaFile)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(metaFile);
    }

    public static boolean supprimerFichier(String utilisateur, String idStockage) throws IOException {
        Path cheminFichier = DOSSIER_RACINE.resolve(utilisateur).resolve(idStockage);
        // Pensez également à retirer la ligne correspondante dans metadata.txt
        return Files.deleteIfExists(cheminFichier);
    }
}
