package de.exp.antrag;

import java.time.LocalDate;

public class Antrag {

    public enum Status { UNBESTAETIGT, OFFEN, ANGENOMMEN, ABGELEHNT }

    private long id;
    private String name;
    private String email;
    private String firma;
    private String grund;
    private LocalDate von;
    private LocalDate bis;
    private Status status = Status.UNBESTAETIGT;
    private String ablehnGrund;

    public Antrag() {
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
}
