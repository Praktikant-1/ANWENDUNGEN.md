package de.exp.schaden;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Schaden extends PanacheEntityBase {

    @Id
    @GeneratedValue
    private Long id;
    // Freitext: mehr Platz als die üblichen 255 Zeichen
    @Column(length = 2000)
    private String beschreibung;
    private LocalDate datum;
    private String melder;
    private String verursacher;


    public Schaden() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
