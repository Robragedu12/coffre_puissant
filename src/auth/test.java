package auth;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import java.util.Scanner;

public class test {

    public static void main(String[] args) {
        try {
            // 1. Test d'inscription (ajout dans users.json avec mot de passe chiffré)
            SignIn signIn = new SignIn();
            signIn.ajoutUtilisateur("monMdpSecret", "bob");
            System.out.println("Utilisateur 'bob' ajouté dans users.json !");

            // 2. Test de connexion via JAAS
            System.setProperty("java.security.auth.login.config", "jaas.config");

            LoginContext lc = new LoginContext("MonAppAuth", callbacks -> {
                Scanner sc = new Scanner(System.in);
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback nc) {
                        System.out.print("Login : ");
                        nc.setName(sc.nextLine());
                    } else if (cb instanceof PasswordCallback pc) {
                        System.out.print("Mot de passe : ");
                        pc.setPassword(sc.nextLine().toCharArray());
                    }
                }
            });

            lc.login();
            System.out.println("Connexion réussie pour : " + lc.getSubject().getPrincipals());
            lc.logout();

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
