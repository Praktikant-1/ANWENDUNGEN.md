package de.exp.antrag;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Antrag extends PanacheEntityBase {

    public enum Status { UNBESTAETIGT, OFFEN, ANGENOMMEN, ABGELEHNT }

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String firma;

    @Column(length = 2000)
    private String grund;
    private LocalDate von;
    private LocalDate bis;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNBESTAETIGT;
    @Column(length = AntragResource.MAX_LAENGE_ABLEHNGRUND)
    private String ablehnGrund;

    private String entschiedenVon;
    private LocalDateTime entschiedenAm;

    public Antrag() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDate getVon() {
        return von;
    }

    public void setVon(LocalDate von) {
        this.von = von;
    }

    public LocalDate getBis() {
        return bis;
    }

    public void setBis(LocalDate bis) {
        this.bis = bis;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAblehnGrund() {
        return ablehnGrund;
    }

    public void setAblehnGrund(String ablehnGrund) {
        this.ablehnGrund = ablehnGrund;
    }

    public String getEntschiedenVon() {
        return entschiedenVon;
    }

    public void setEntschiedenVon(String entschiedenVon) {
        this.entschiedenVon = entschiedenVon;
    }

    public LocalDateTime getEntschiedenAm() {
        return entschiedenAm;
    }

    public void setEntschiedenAm(LocalDateTime entschiedenAm) {
        this.entschiedenAm = entschiedenAm;
    }
}
