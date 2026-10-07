package de.exp;

import java.time.LocalDate;
import java.time.LocalTime;

public class Besucher {

    private long id;
    private String name;
    private String firma;
    private String grund;
    // Besuchstag; bei mehrtägigen Besuchen (aus einem Antrag) zusätzlich das Enddatum
    private LocalDate datum;
    private LocalDate bis;
    private LocalTime ankunft;
    private LocalTime austritt;

    public Besucher() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getGrund() {
        return grund;
    }

    public void setGrund(String grund) {
        this.grund = grund;
    }

    public LocalTime getAnkunft() {
        return ankunft;
    }

    public void setAnkunft(LocalTime ankunft) {
        this.ankunft = ankunft;
    }

    public LocalTime getAustritt() {
        return austritt;
    }

    public void setAustritt(LocalTime austritt) {
        this.austritt = austritt;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalDate getBis() {
        return bis;
    }

    public void setBis(LocalDate bis) {
        this.bis = bis;
    }
}
