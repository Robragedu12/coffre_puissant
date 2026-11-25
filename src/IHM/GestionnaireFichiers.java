package IHM;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Maquette IHM (front uniquement) : liste de fichiers + Ajouter / Obtenir / Supprimer.
 * Compilation : javac GestionnaireFichiers.java
 * Exécution   : java GestionnaireFichiers [idUtilisateur]
 */
public class GestionnaireFichiers extends JFrame {

    private final DefaultListModel<String[]> modele = new DefaultListModel<>();
    private final JList<String[]> liste = new JList<>(modele);

    public GestionnaireFichiers(String idUtilisateur) {
        super("Gestionnaire de fichiers");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contenu = new JPanel(new BorderLayout(10, 10));
        contenu.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contenu);

        // En-tête
        JLabel titre = new JLabel("Bienvenu : " + idUtilisateur, SwingConstants.CENTER);
        titre.setFont(titre.getFont().deriveFont(Font.BOLD, 14f));
        contenu.add(titre, BorderLayout.NORTH);

        // Données d'exemple {nom, taille}
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"Mon fichier", "40ko"});
        modele.addElement(new String[]{"", ""});
        modele.addElement(new String[]{"", ""});

        // Liste avec défilement, sélection par un clic
        liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        liste.setCellRenderer(new LigneRenderer());
        liste.setFixedCellHeight(34);
        JScrollPane scroll = new JScrollPane(liste);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroll.setPreferredSize(new Dimension(450, 230));
        contenu.add(scroll, BorderLayout.CENTER);

        // Boutons (aucune action pour le moment)
        JPanel boutons = new JPanel(new GridLayout(1, 3, 25, 0));
        boutons.setBorder(new EmptyBorder(10, 0, 5, 0));
        for (String nom : new String[]{"Ajouter", "Obtenir", "Supprimer"}) {
            JButton b = new JButton(nom);
            b.setPreferredSize(new Dimension(100, 55));
            boutons.add(b);
        }
        contenu.add(boutons, BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    /** Ligne : nom à gauche, taille à droite, bordure arrondie. */
    private static class LigneRenderer implements ListCellRenderer<String[]> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list,
                                                      String[] v, int index, boolean selected, boolean focus) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(2, 4, 2, 4),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 2, true),
                            new EmptyBorder(3, 10, 3, 10))));
            p.add(new JLabel(v[0].isEmpty() ? " " : v[0]), BorderLayout.WEST);
            p.add(new JLabel(v[1]), BorderLayout.EAST);
            p.setBackground(selected ? new Color(184, 207, 229) : Color.WHITE);
            p.setOpaque(true);
            return p;
        }
    }

    public static void main(String[] args) {
        String id = args.length > 0 ? args[0] : "utilisateur";
        SwingUtilities.invokeLater(() -> new GestionnaireFichiers(id).setVisible(true));
    }
}