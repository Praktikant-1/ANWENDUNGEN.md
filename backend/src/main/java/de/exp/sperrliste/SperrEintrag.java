package de.exp.sperrliste;

import java.time.LocalDateTime;

// Ein Eintrag pro E-Mail-Adresse. Bleibt auch nach Ablauf oder Aufhebung der Sperre
// in der Liste, damit man sieht, wie oft die Adresse schon gesperrt wurde.
public class SperrEintrag {

    private String email;
    private int anzahlSperren;
    private LocalDateTime gesperrtAm;
    // null = dauerhaft gesperrt
    private LocalDateTime gesperrtBis;
    // Wer (Benutzername) die letzte Sperre wann vorzeitig aufgehoben hat
    private String aufgehobenVon;
    private LocalDateTime aufgehobenAm;
    // Wird beim Abrufen der Liste gesetzt
    private boolean gesperrt;

    public SperrEintrag() {
    }

    public SperrEintrag(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAnzahlSperren() {
        return anzahlSperren;
    }

    public void setAnzahlSperren(int anzahlSperren) {
        this.anzahlSperren = anzahlSperren;
    }

    public LocalDateTime getGesperrtAm() {
        return gesperrtAm;
    }

    public void setGesperrtAm(LocalDateTime gesperrtAm) {
        this.gesperrtAm = gesperrtAm;
    }

    public LocalDateTime getGesperrtBis() {
        return gesperrtBis;
    }

    public void setGesperrtBis(LocalDateTime gesperrtBis) {
        this.gesperrtBis = gesperrtBis;
    }

    public String getAufgehobenVon() {
        return aufgehobenVon;
    }

    public void setAufgehobenVon(String aufgehobenVon) {
        this.aufgehobenVon = aufgehobenVon;
    }

    public LocalDateTime getAufgehobenAm() {
        return aufgehobenAm;
    }

    public void setAufgehobenAm(LocalDateTime aufgehobenAm) {
        this.aufgehobenAm = aufgehobenAm;
    }

    public boolean isGesperrt() {
        return gesperrt;
    }

    public void setGesperrt(boolean gesperrt) {
        this.gesperrt = gesperrt;
    }
}
