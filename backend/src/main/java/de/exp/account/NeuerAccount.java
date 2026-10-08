package de.exp.account;

public class NeuerAccount {

    private String benutzername;
    private String passwort;
    private Account.Rolle rolle;
    private boolean admin;

    public NeuerAccount() {
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public Account.Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Account.Rolle rolle) {
        this.rolle = rolle;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
