package de.exp.account;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import java.util.List;

// @Transactional an der Klasse: jede Methode läuft in einer Transaktion,
// so funktioniert die Datenbank auch beim Login (dort gibt es sonst keine)
@ApplicationScoped
@Transactional
public class AccountService {

    // Start-Accounts, aber nur wenn es noch gar keine Accounts gibt (nur zum Entwickeln)
    void startAccountsAnlegen(@Observes StartupEvent start) {
        if (Account.count() > 0) {
            return;
        }
        erstellen("LeonhardKlotz", "leo123", Account.Rolle.MITARBEITER, false);
        erstellen("Nicole", "nicole123", Account.Rolle.VERWALTUNG, false);
        erstellen("Markus", "markus123", Account.Rolle.VERWALTUNG, true);
    }

    public List<Account> alle() {
        return Account.listAll(Sort.by("benutzername"));
    }

    public Account finde(String benutzername) {
        return Account.findById(benutzername);
    }

    // Gibt null zurück, wenn es den Benutzernamen schon gibt
    public Account erstellen(String benutzername, String passwort, Account.Rolle rolle, boolean admin) {
        if (Account.findById(benutzername) != null) {
            return null;
        }
        Account account = new Account(benutzername, BcryptUtil.bcryptHash(passwort), rolle, admin);
        account.persist();
        return account;
    }

    // Gibt den Account zurück, wenn Benutzername und Passwort stimmen, sonst null
    public Account pruefeLogin(String benutzername, String passwort) {
        Account account = Account.findById(benutzername);
        if (account == null || !BcryptUtil.matches(passwort, account.getPasswortHash())) {
            return null;
        }
        return account;
    }

    public boolean loeschen(String benutzername) {
        return Account.deleteById(benutzername);
    }
}
