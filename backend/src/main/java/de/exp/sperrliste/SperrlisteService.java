package de.exp.sperrliste;

import de.exp.mail.MailService;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Spamschutz: Wer mit derselben E-Mail-Adresse zu viele Anfragen schickt, wird gesperrt.
// 1. Sperre: 24 Stunden, 2. Sperre: 1 Monat, ab der 3. Sperre: dauerhaft.
// Die Sperren stehen in der Datenbank, die Versuche der letzten 30 Minuten nur im Speicher
// (nach einem Neustart fängt das Zählen einfach neu an).
// @Singleton statt @ApplicationScoped, damit der Test uhr und mailService austauschen kann
@Singleton
@Transactional
public class SperrlisteService {

    static final int MAX_VERSUCHE = 15;
    static final Duration ZEITRAUM = Duration.ofMinutes(30);

    @Inject
    MailService mailService;

    // Austauschbar, damit Tests die Zeit vorspulen können
    Clock uhr = Clock.systemDefaultZone();

    // Nicht private, damit der Test die Versuche zurücksetzen kann
    final Map<String, Deque<LocalDateTime>> versuche = new HashMap<>();

    // Zählt einen Versuch (Antrag stellen, Code anfordern) für diese Adresse.
    // Gibt false zurück, wenn die Adresse gesperrt ist und die Anfrage abgelehnt werden soll.
    public boolean versuchErlaubt(String email) {
        String adresse = MailService.normalisieren(email);
        SperrEintrag neueSperre;
        synchronized (this) {
            if (istGesperrt(adresse)) {
                return false;
            }
            LocalDateTime jetzt = LocalDateTime.now(uhr);
            Deque<LocalDateTime> zeiten = versuche.computeIfAbsent(adresse, key -> new ArrayDeque<>());
            while (!zeiten.isEmpty() && !zeiten.peekFirst().isAfter(jetzt.minus(ZEITRAUM))) {
                zeiten.pollFirst();
            }
            zeiten.addLast(jetzt);
            if (zeiten.size() < MAX_VERSUCHE) {
                return true;
            }
            // Der 15. Versuch geht noch durch, alle weiteren werden abgelehnt
            versuche.remove(adresse);
            neueSperre = sperren(adresse, jetzt);
        }
        try {
            mailService.sperreSenden(adresse, neueSperre.getAnzahlSperren(), neueSperre.getGesperrtBis());
        } catch (RuntimeException e) {
            // Die Sperre gilt trotzdem
            Log.errorf(e, "Sperr-Mail an %s konnte nicht gesendet werden", adresse);
        }
        return true;
    }

    public synchronized boolean istGesperrt(String email) {
        SperrEintrag eintrag = SperrEintrag.findById(MailService.normalisieren(email));
        return eintrag != null && aktiv(eintrag);
    }

    // Neueste Sperren zuerst. Kopien, damit "gesperrt" nicht im gespeicherten Eintrag landet.
    public synchronized List<SperrEintrag> alle() {
        List<SperrEintrag> eintraege = SperrEintrag.listAll();
        return eintraege.stream()
                .map(this::kopieMitStatus)
                .sorted(Comparator.comparing(SperrEintrag::getGesperrtAm).reversed())
                .toList();
    }

    // Hebt die aktuelle Sperre auf. Der Eintrag (und die Anzahl der Sperren) bleibt erhalten,
    // die nächste Sperre wird also trotzdem länger. Gibt null zurück, wenn nichts gesperrt ist.
    public synchronized SperrEintrag aufheben(String email, String benutzername) {
        SperrEintrag eintrag = SperrEintrag.findById(MailService.normalisieren(email));
        if (eintrag == null || !aktiv(eintrag)) {
            return null;
        }
        eintrag.setAufgehobenVon(benutzername);
        eintrag.setAufgehobenAm(LocalDateTime.now(uhr).truncatedTo(ChronoUnit.MINUTES));
        return kopieMitStatus(eintrag);
    }

    private SperrEintrag sperren(String adresse, LocalDateTime jetzt) {
        SperrEintrag eintrag = SperrEintrag.findById(adresse);
        if (eintrag == null) {
            eintrag = new SperrEintrag(adresse);
            eintrag.persist();
        }
        int anzahl = eintrag.getAnzahlSperren() + 1;
        LocalDateTime beginn = jetzt.truncatedTo(ChronoUnit.MINUTES);
        eintrag.setAnzahlSperren(anzahl);
        eintrag.setGesperrtAm(beginn);
        eintrag.setGesperrtBis(switch (anzahl) {
            case 1 -> beginn.plusHours(24);
            case 2 -> beginn.plusMonths(1);
            default -> null;
        });
        eintrag.setAufgehobenVon(null);
        eintrag.setAufgehobenAm(null);
        Log.infof("E-Mail-Adresse %s gesperrt (%d. Sperre)", adresse, anzahl);
        return kopieMitStatus(eintrag);
    }

    private boolean aktiv(SperrEintrag eintrag) {
        return eintrag.getAufgehobenAm() == null
                && (eintrag.getGesperrtBis() == null || LocalDateTime.now(uhr).isBefore(eintrag.getGesperrtBis()));
    }

    private SperrEintrag kopieMitStatus(SperrEintrag eintrag) {
        SperrEintrag kopie = new SperrEintrag(eintrag.getEmail());
        kopie.setAnzahlSperren(eintrag.getAnzahlSperren());
        kopie.setGesperrtAm(eintrag.getGesperrtAm());
        kopie.setGesperrtBis(eintrag.getGesperrtBis());
        kopie.setAufgehobenVon(eintrag.getAufgehobenVon());
        kopie.setAufgehobenAm(eintrag.getAufgehobenAm());
        kopie.setGesperrt(aktiv(eintrag));
        return kopie;
    }
}
