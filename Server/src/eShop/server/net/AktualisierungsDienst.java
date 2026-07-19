package eShop.server.net;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AktualisierungsDienst {
    private final Map<PrintWriter, String> empfaenger = new ConcurrentHashMap<>();

    public void anmelden(String clientKennung, PrintWriter ausgabe) {
        empfaenger.put(ausgabe, clientKennung);
    }

    public void abmelden(PrintWriter ausgabe) {
        if (ausgabe != null) {
            empfaenger.remove(ausgabe);
        }
    }

    public void meldeAenderung(String ursprungsClient, String bereich) {
        String nachricht = "AKTUALISIERUNG:" + bereich;
        empfaenger.forEach((ausgabe, clientKennung) -> {
            if (!clientKennung.equals(ursprungsClient)) {
                ausgabe.println(nachricht);
            }
        });
    }
}
