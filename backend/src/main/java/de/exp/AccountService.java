package de.exp;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AccountService {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    // Start-Accounts, die es nach jedem Neustart gibt (nur zum Entwickeln)
    public AccountService() {
        erstellen("mitarbeiter", "mitarbeiter123", Account.Rolle.MITARBEITER, false);
        erstellen("verwaltung", "verwaltung123", Account.Rolle.VERWALTUNG, false);
        erstellen("admin", "admin123", Account.Rolle.VERWALTUNG, true);
    }

    public List<Account> alle() {
        return new ArrayList<>(accounts.values());
    }

    public Account finde(String benutzername) {
        return accounts.get(benutzername);
    }

    // Gibt null zurück, wenn es den Benutzernamen schon gibt
    public Account erstellen(String benutzername, String passwort, Account.Rolle rolle, boolean admin) {
        Account account = new Account(benutzername, BcryptUtil.bcryptHash(passwort), rolle, admin);
        Account vorhanden = accounts.putIfAbsent(benutzername, account);
        return vorhanden == null ? account : null;
    }

    // Gibt den Account zurück, wenn Benutzername und Passwort stimmen, sonst null
    public Account pruefeLogin(String benutzername, String passwort) {
        Account account = accounts.get(benutzername);
        if (account == null || !BcryptUtil.matches(passwort, account.getPasswortHash())) {
            return null;
        }
        return account;
    }

    public boolean loeschen(String benutzername) {
        return accounts.remove(benutzername) != null;
    }
}
