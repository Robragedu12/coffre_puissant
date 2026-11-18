package auth;

import javax.security.auth.callback.*;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class Controleur implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback nc) {
                System.out.print(nc.getPrompt());
                nc.setName(scanner.nextLine());
            } else if (callback instanceof PasswordCallback pc) {
                System.out.print(pc.getPrompt());
                if (console != null) {
                    // Masque la saisie dans un vrai terminal
                    pc.setPassword(console.readPassword());
                } else {
                    // Fallback pour les consoles d'IDE (IntelliJ, Eclipse)
                    pc.setPassword(scanner.nextLine().toCharArray());
                }
            } else {
                throw new UnsupportedCallbackException(callback, "Callback non supporté");
            }
        }
    }
}
