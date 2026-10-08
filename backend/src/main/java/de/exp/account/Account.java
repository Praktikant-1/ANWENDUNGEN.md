package de.exp.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.util.HashSet;
import java.util.Set;

// @Entity: Jeder Account wird als Zeile in der Tabelle "Account" gespeichert
@Entity
public class Account extends PanacheEntityBase {

    public enum Rolle { MITARBEITER, VERWALTUNG }

    // Der Benutzername ist eindeutig und dient deshalb direkt als Schlüssel
    @Id
    private String benutzername;
    private String passwortHash;
    // Als Text speichern ("VERWALTUNG"), nicht als Zahl
    @Enumerated(EnumType.STRING)
    private Rolle rolle;
    private boolean admin;

    public Account() {
    }

    public Account(String benutzername, String passwortHash, Rolle rolle, boolean admin) {
        this.benutzername = benutzername;
        this.passwortHash = passwortHash;
        this.rolle = rolle;
        this.admin = admin;
    }

    // Rollen für @RolesAllowed, z. B. ["verwaltung", "admin"]
    @JsonIgnore
    public Set<String> getRollen() {
        Set<String> rollen = new HashSet<>();
        rollen.add(rolle.name().toLowerCase());
        if (admin) {
            rollen.add("admin");
        }
        return rollen;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    // Der Hash wird nie an den Browser geschickt
    @JsonIgnore
    public String getPasswortHash() {
        return passwortHash;
    }

    public void setPasswortHash(String passwortHash) {
        this.passwortHash = passwortHash;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
