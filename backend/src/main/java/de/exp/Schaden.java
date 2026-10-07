package de.exp;

import java.time.LocalDate;

public class Schaden {

    private long id;
    private String beschreibung;
    private LocalDate datum;
    private String melder;
    private String verursacher;

    public Schaden() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public String getMelder() {
        return melder;
    }

    public void setMelder(String melder) {
        this.melder = melder;
    }

    // Nur ausfüllen, wenn jemand den Schaden zugegeben hat
    public String getVerursacher() {
        return verursacher;
    }

    public void setVerursacher(String verursacher) {
        this.verursacher = verursacher;
    }
}
