package de.exp;

import java.time.LocalTime;

public class Besucher {

    private String name;
    private String firma;
    private String grund;
    private LocalTime ankunft;

    public Besucher() {
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
}
