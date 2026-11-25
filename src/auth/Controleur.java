package auth;

import javax.security.auth.callback.*;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class Controleur implements CallbackHandler {

    private String id;
    private char[] mdp;

    public Controleur(String id, char[] mdp) {
        this.id = id;
        this.mdp = mdp;
    }

    public Controleur() {
    }

    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        for (Callback callback : callbacks) {

            if (callback instanceof NameCallback nc) {
                nc.setName(id);

            } else if (callback instanceof PasswordCallback pc) {
                pc.setPassword(mdp);

            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
