package de.exp;

import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MailService {

    // Muss zum Countdown auf der Startseite passen
    static final int VERZOEGERUNG_SEKUNDEN = 30;
    static final int CODE_GUELTIG_MINUTEN = 10;
    static final int MAX_FEHLVERSUCHE = 5;

    private record Code(String wert, Instant gueltigBis, int fehlversuche) {
    }

    @Inject
    Mailer mailer;

    private final SecureRandom zufall = new SecureRandom();
    private final ScheduledExecutorService zeitplaner = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, ScheduledFuture<?>> geplanteMails = new ConcurrentHashMap<>();
    private final Map<String, Code> codes = new ConcurrentHashMap<>();

    // Debouncing: Kommt für dieselbe Adresse innerhalb der Wartezeit eine neue Anfrage,
    // wird die geplante Mail verworfen und die Wartezeit beginnt von vorn.
    public void codeAnfordern(String email) {
        String adresse = normalisieren(email);
        geplanteMails.compute(adresse, (key, alteMail) -> {
            if (alteMail != null) {
                alteMail.cancel(false);
            }
            return zeitplaner.schedule(() -> codeSenden(adresse), VERZOEGERUNG_SEKUNDEN, TimeUnit.SECONDS);
        });
    }

    public boolean codePruefen(String email, String eingabe) {
        String adresse = normalisieren(email);
        Code code = codes.get(adresse);
        if (code == null || Instant.now().isAfter(code.gueltigBis())) {
            codes.remove(adresse);
            return false;
        }
        if (code.wert().equals(eingabe == null ? "" : eingabe.trim())) {
            codes.remove(adresse);
            return true;
        }
        // Gegen Durchprobieren: nach zu vielen Fehlversuchen ist der Code verbraucht
        if (code.fehlversuche() + 1 >= MAX_FEHLVERSUCHE) {
            codes.remove(adresse);
        } else {
            codes.put(adresse, new Code(code.wert(), code.gueltigBis(), code.fehlversuche() + 1));
        }
        return false;
    }

    public void entscheidungSenden(Antrag antrag, String ablehnGrund) {
        boolean angenommen = antrag.getStatus() == Antrag.Status.ANGENOMMEN;
        StringBuilder text = new StringBuilder()
                .append("Hello ").append(antrag.getName()).append(",\n\n")
                .append("your visit request for ").append(antrag.getVon())
                .append(" – ").append(antrag.getBis())
                .append(angenommen ? " has been accepted." : " has been rejected.")
                .append("\n");
        if (!angenommen && ablehnGrund != null && !ablehnGrund.isBlank()) {
            text.append("\nReason: ").append(ablehnGrund.trim()).append("\n");
        }
        text.append("\nEXPass Visitor Management");

        mailer.send(Mail.withText(antrag.getEmail(),
                "EXPass – Your visit request was " + (angenommen ? "accepted" : "rejected"),
                text.toString()));
    }

    private void codeSenden(String adresse) {
        String wert = String.format("%06d", zufall.nextInt(1_000_000));
        codes.put(adresse, new Code(wert, Instant.now().plusSeconds(CODE_GUELTIG_MINUTEN * 60L), 0));

        try {
            mailer.send(Mail.withText(adresse,
                    "EXPass – Your confirmation code",
                    "Your confirmation code is: " + wert + "\n\n"
                            + "It is valid for " + CODE_GUELTIG_MINUTEN + " minutes.\n\n"
                            + "EXPass Visitor Management"));
        } catch (RuntimeException e) {
            // Läuft im Hintergrund, ein Fehler würde sonst nirgends auftauchen
            Log.errorf(e, "Bestätigungscode an %s konnte nicht gesendet werden", adresse);
        }
    }

    static String normalisieren(String email) {
        return email.trim().toLowerCase();
    }

    @PreDestroy
    void beenden() {
        zeitplaner.shutdownNow();
    }
}
