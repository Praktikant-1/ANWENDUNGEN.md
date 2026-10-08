package de.exp.sperrliste;

import de.exp.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SperrlisteServiceTest {

    private static final String EMAIL = "spam@example.com";

    // Merkt sich die Sperr-Mails, statt sie zu verschicken
    static class TestMailService extends MailService {
        final List<String> sperrMails = new ArrayList<>();

        @Override
        public void sperreSenden(String adresse, int anzahlSperren, LocalDateTime gesperrtBis) {
            sperrMails.add(adresse + " " + anzahlSperren);
        }
    }

    private SperrlisteService service;
    private TestMailService mails;
    private Instant jetzt;

    @BeforeEach
    void vorbereiten() {
        mails = new TestMailService();
        service = new SperrlisteService();
        service.mailService = mails;
        jetzt = Instant.parse("2026-10-08T10:00:00Z");
        stelleUhr();
    }

    private void stelleUhr() {
        service.uhr = Clock.fixed(jetzt, ZoneOffset.UTC);
    }

    private void vorspulen(Duration dauer) {
        jetzt = jetzt.plus(dauer);
        stelleUhr();
    }

    private void versuche(int anzahl) {
        for (int i = 0; i < anzahl; i++) {
            assertTrue(service.versuchErlaubt(EMAIL), "Versuch " + (i + 1) + " sollte erlaubt sein");
        }
    }

    @Test
    void nach15VersuchenIn30MinutenGesperrt() {
        versuche(14);
        assertFalse(service.istGesperrt(EMAIL));
        assertTrue(mails.sperrMails.isEmpty());

        versuche(1);
        assertTrue(service.istGesperrt(EMAIL));
        assertFalse(service.versuchErlaubt(EMAIL));
        assertEquals(List.of(EMAIL + " 1"), mails.sperrMails);
    }

    @Test
    void grossKleinschreibungZaehltAlsDieselbeAdresse() {
        versuche(14);
        assertTrue(service.versuchErlaubt("  SPAM@Example.com "));
        assertTrue(service.istGesperrt(EMAIL));
    }

    @Test
    void alteVersucheAusserhalbDes30MinutenZeitraumsZaehlenNicht() {
        versuche(14);
        vorspulen(Duration.ofMinutes(31));
        versuche(14);
        assertFalse(service.istGesperrt(EMAIL));
    }

    @Test
    void ersteSperre24StundenZweiteEinMonatDritteDauerhaft() {
        versuche(15);
        SperrEintrag erste = service.alle().getFirst();
        assertEquals(1, erste.getAnzahlSperren());
        assertEquals(erste.getGesperrtAm().plusHours(24), erste.getGesperrtBis());

        vorspulen(Duration.ofHours(24));
        assertFalse(service.istGesperrt(EMAIL));
        versuche(15);
        SperrEintrag zweite = service.alle().getFirst();
        assertEquals(2, zweite.getAnzahlSperren());
        assertEquals(zweite.getGesperrtAm().plusMonths(1), zweite.getGesperrtBis());

        vorspulen(Duration.ofDays(31));
        assertFalse(service.istGesperrt(EMAIL));
        versuche(15);
        SperrEintrag dritte = service.alle().getFirst();
        assertEquals(3, dritte.getAnzahlSperren());
        assertNull(dritte.getGesperrtBis());

        vorspulen(Duration.ofDays(3650));
        assertTrue(service.istGesperrt(EMAIL));
        assertEquals(List.of(EMAIL + " 1", EMAIL + " 2", EMAIL + " 3"), mails.sperrMails);
    }

    @Test
    void adminHebtSperreAufEintragBleibtInDerListe() {
        versuche(15);
        SperrEintrag aufgehoben = service.aufheben(EMAIL, "Markus");
        assertNotNull(aufgehoben);
        assertEquals("Markus", aufgehoben.getAufgehobenVon());
        assertFalse(service.istGesperrt(EMAIL));
        assertTrue(service.versuchErlaubt(EMAIL));

        SperrEintrag eintrag = service.alle().getFirst();
        assertFalse(eintrag.isGesperrt());
        assertEquals(1, eintrag.getAnzahlSperren());

        // Nächster Verstoß zählt als 2. Sperre
        versuche(14);
        assertTrue(service.istGesperrt(EMAIL));
        assertEquals(2, service.alle().getFirst().getAnzahlSperren());
        assertNull(service.alle().getFirst().getAufgehobenVon());
    }

    @Test
    void aufhebenOhneAktiveSperreGibtNull() {
        assertNull(service.aufheben(EMAIL, "Markus"));
        versuche(15);
        vorspulen(Duration.ofHours(25));
        assertNull(service.aufheben(EMAIL, "Markus"));
    }

}
