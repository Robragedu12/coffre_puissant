package reseau;

import java.net.*;
import java.util.*;

public class AdresseServeurAuto {

    /**
     * Cherche la première adresse IPv4 de l'interface dont le displayName contient partialName.
     * @param partialName Chaîne contenue dans le displayName de l'interface recherchée
     * @return L'adresse IPv4 sous forme de chaîne, ou null si aucune trouvée.
     * @throws SocketException
     */
    public static String getIPv4(String partialName) throws SocketException {

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {

            if (netint.getDisplayName().contains(partialName)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        return null;
    }
}  