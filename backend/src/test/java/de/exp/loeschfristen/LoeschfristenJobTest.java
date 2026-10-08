package de.exp.loeschfristen;

import de.exp.antrag.Antrag;
import de.exp.besucher.Besucher;
import de.exp.schaden.Schaden;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// @QuarkusTest: startet die App mit einer Test-Datenbank (Docker)
@QuarkusTest
class LoeschfristenJobTest {

    private static final LocalDate HEUTE = LocalDate.of(2026, 10, 8);

    @Inject
    LoeschfristenJob job;

    @Test
    void loeschtNurWasAelterAls5JahreIst() {
        QuarkusTransaction.requiringNew().run(() -> {
            Besucher.deleteAll();
            Antrag.deleteAll();
            Schaden.deleteAll();

            besucher("6 Jahre alt", HEUTE.minusYears(6), null);
            besucher("genau 5 Jahre alt", HEUTE.minusYears(5), null);
            besucher("mehrtägig, endet vor 4 Jahren", HEUTE.minusYears(6), HEUTE.minusYears(4));
            antrag("6 Jahre alt", HEUTE.minusYears(6));
            antrag("gestern", HEUTE.minusDays(1));
            schaden("5 Jahre und 1 Tag alt", HEUTE.minusYears(5).minusDays(1));
            schaden("heute", HEUTE);
        });

        assertEquals(3, job.alteDatenLoeschen(HEUTE));

        assertEquals(List.of("genau 5 Jahre alt", "mehrtägig, endet vor 4 Jahren"),
                QuarkusTransaction.requiringNew().call(() ->
                        Besucher.<Besucher>listAll().stream().map(Besucher::getName).sorted().toList()));
        assertEquals(List.of("gestern"),
                QuarkusTransaction.requiringNew().call(() ->
                        Antrag.<Antrag>listAll().stream().map(Antrag::getName).toList()));
        assertEquals(List.of("heute"),
                QuarkusTransaction.requiringNew().call(() ->
                        Schaden.<Schaden>listAll().stream().map(Schaden::getBeschreibung).toList()));
    }

    private static void besucher(String name, LocalDate datum, LocalDate bis) {
        Besucher besucher = new Besucher();
        besucher.setName(name);
        besucher.setDatum(datum);
        besucher.setBis(bis);
        besucher.persist();
    }

    private static void antrag(String name, LocalDate bis) {
        Antrag antrag = new Antrag();
        antrag.setName(name);
        antrag.setVon(bis);
        antrag.setBis(bis);
        antrag.persist();
    }

    private static void schaden(String beschreibung, LocalDate datum) {
        Schaden schaden = new Schaden();
        schaden.setBeschreibung(beschreibung);
        schaden.setDatum(datum);
        schaden.persist();
    }
}
