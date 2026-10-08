package de.exp.loeschfristen;

import de.exp.antrag.Antrag;
import de.exp.besucher.Besucher;
import de.exp.schaden.Schaden;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;

// Löscht jede Nacht Daten, deren Aufbewahrungsfrist abgelaufen ist (siehe ANFORDERUNGEN.md):
// Besuche, Anträge und Schäden werden 5 Jahre aufbewahrt.
// Bestätigungscodes (10 Minuten) liegen nur im Speicher, die Sperrliste bleibt dauerhaft.
@ApplicationScoped
public class LoeschfristenJob {

    static final Period AUFBEWAHRUNG = Period.ofYears(5);

    // Jede Nacht um 3 Uhr
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    void jedeNacht() {
        alteDatenLoeschen(LocalDate.now());
    }

    // Eigene Methode mit "heute" als Parameter, damit der Test ein Datum vorgeben kann.
    // Gezählt wird ab dem Ende des Besuchs (bei mehrtägigen Besuchen also ab "bis").
    @Transactional
    long alteDatenLoeschen(LocalDate heute) {
        LocalDate grenze = heute.minus(AUFBEWAHRUNG);
        long besucher = Besucher.delete("coalesce(bis, datum) < ?1", grenze);
        long antraege = Antrag.delete("bis < ?1", grenze);
        long schaeden = Schaden.delete("datum < ?1", grenze);
        Log.infof("Löschfristen: %d Besucher, %d Anträge, %d Schäden älter als %s gelöscht",
                besucher, antraege, schaeden, grenze);
        return besucher + antraege + schaeden;
    }
}
