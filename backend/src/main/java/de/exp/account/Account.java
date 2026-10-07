package de.exp.account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public class Account {

    public enum Rolle { MITARBEITER, VERWALTUNG }

    private String benutzername;
    private String passwortHash;
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
