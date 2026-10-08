package de.exp.sperrliste;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;


@Entity
public class SperrEintrag extends PanacheEntityBase {

    @Id
    private String email;
    private int anzahlSperren;
    private LocalDateTime gesperrtAm;

    private LocalDateTime gesperrtBis;

    private String aufgehobenVon;
    private LocalDateTime aufgehobenAm;

    @Transient
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
