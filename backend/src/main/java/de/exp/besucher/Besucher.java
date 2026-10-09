package de.exp.besucher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Besucher extends PanacheEntityBase {

    // Die Nummer vergibt die Datenbank selbst
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String firma;

    @Column(length = 100)

    private String grund;
    private LocalDate datum;
    private LocalDate bis;
    private LocalTime ankunft;
    private LocalTime austritt;
    // Zufälliger Code für den QR-Code im Besuchsausweis; wird nicht an den Browser geschickt
    @JsonIgnore
    @Column(unique = true)
    private String qrCode;

    public Besucher() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
