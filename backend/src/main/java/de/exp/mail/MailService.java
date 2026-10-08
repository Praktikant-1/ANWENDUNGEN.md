package de.exp.mail;

import de.exp.antrag.Antrag;
import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MailService {

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

    public void entscheidungSenden(Antrag antrag) {
        boolean angenommen = antrag.getStatus() == Antrag.Status.ANGENOMMEN;
        StringBuilder text = new StringBuilder()
                .append("Hello ").append(antrag.getName()).append(",\n\n")
                .append("your visit request for ").append(antrag.getVon())
                .append(" – ").append(antrag.getBis())
                .append(angenommen ? " has been accepted." : " has been rejected.")
                .append("\n");
        if (!angenommen && antrag.getAblehnGrund() != null) {
            text.append("\nReason: ").append(antrag.getAblehnGrund()).append("\n");
        }
        text.append("\nEXPass Visitor Management");

        mailer.send(Mail.withText(antrag.getEmail(),
                "EXPass – Your visit request was " + (angenommen ? "accepted" : "rejected"),
                text.toString()));
    }

    // 1. Sperre: 24 Stunden, 2. Sperre: 1 Monat, ab der 3.: dauerhaft (gesperrtBis == null)
    public void sperreSenden(String adresse, int anzahlSperren, LocalDateTime gesperrtBis) {
        String dauer = switch (anzahlSperren) {
            case 1 -> "for 24 hours (until " + gesperrtBis.toString().replace('T', ' ') + ")";
            case 2 -> "for one month (until " + gesperrtBis.toLocalDate() + ")";
            default -> "permanently";
        };
        mailer.send(Mail.withText(adresse,
                "EXPass – Your e-mail address has been blocked",
                "Hello,\n\n"
                        + "too many visit requests were sent with this e-mail address in a short time.\n"
                        + "It has therefore been blocked " + dauer + ".\n"
                        + "During this time no visit requests can be made with it.\n\n"
                        + "If you think this is a mistake, please contact us.\n\n"
                        + "EXPass Visitor Management"));
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

    public static String normalisieren(String email) {
        return email.trim().toLowerCase();
    }

    @PreDestroy
    void beenden() {
        zeitplaner.shutdownNow();
    }
}
