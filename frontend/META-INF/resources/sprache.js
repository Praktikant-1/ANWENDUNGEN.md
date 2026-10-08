// Sprache umschalten (Deutsch/Englisch) für alle Seiten.
//
// Im HTML:        <h2 data-i18n="schluessel">…</h2>          -> ersetzt den Text
//                 <input data-i18n-placeholder="schluessel">  -> ersetzt den Platzhalter
//                 <input data-i18n-title="schluessel">        -> ersetzt den Tooltip
// Im JavaScript:  t('schluessel') bzw. t('schluessel', { name: 'Max' }) für {name} im Text
// Umschalter:     <div class="sprachwahl"></div> wird mit den Knöpfen DE | EN gefüllt
//
// Nach einem Wechsel wird das Ereignis "sprachwechsel" ausgelöst, damit Seiten
// ihre per JavaScript erzeugten Inhalte (Tabellen usw.) neu aufbauen können.

// Jeder Text steht als [Deutsch, Englisch] in einer Zeile, so kann keine Übersetzung fehlen
const TEXTE = {
    // Allgemein
    'abmelden': ['Abmelden', 'Log out'],
    'abbrechen': ['Abbrechen', 'Cancel'],
    'bestaetigen': ['Bestätigen', 'Confirm'],
    'ja': ['Ja', 'Yes'],
    'nein': ['Nein', 'No'],
    'treffer.eins': ['1 Treffer', '1 match'],
    'treffer.viele': ['{n} Treffer', '{n} matches'],
    'zeit.leer': ['Leer lassen für aktuelle Uhrzeit', 'Leave empty for current time'],

    // Felder und Tabellenspalten
    'feld.name': ['Name', 'Name'],
    'feld.email': ['E-Mail', 'E-mail'],
    'feld.firma': ['Firma', 'Company'],
    'feld.optional': ['(optional)', '(optional)'],
    'feld.grund': ['Grund des Besuchs', 'Reason for visit'],
    'feld.benutzername': ['Benutzername', 'Username'],
    'feld.passwort': ['Passwort', 'Password'],
    'spalte.grund': ['Grund', 'Reason'],
    'spalte.von': ['Von', 'From'],
    'spalte.bis': ['Bis', 'Until'],
    'spalte.status': ['Status', 'Status'],
    'spalte.entscheidung': ['Entscheidung', 'Decision'],
    'spalte.datum': ['Datum', 'Date'],
    'spalte.beschreibung': ['Beschreibung', 'Description'],
    'spalte.gemeldetVon': ['Gemeldet von', 'Reported by'],
    'spalte.verursacher': ['Verursacher', 'Caused by'],
    'spalte.ankunft': ['Ankunft', 'Arrival'],
    'spalte.austritt': ['Austritt', 'Departure'],
    'spalte.rolle': ['Rolle', 'Role'],
    'spalte.admin': ['Admin', 'Admin'],
    'status.OFFEN': ['Offen', 'Open'],
    'status.ANGENOMMEN': ['Angenommen', 'Accepted'],
    'status.ABGELEHNT': ['Abgelehnt', 'Rejected'],
    'rolle.MITARBEITER': ['Mitarbeiter', 'Employee'],
    'rolle.VERWALTUNG': ['Verwaltung', 'Administration'],

    // Startseite
    'start.login': ['Mitarbeiter-Login', 'Staff login'],
    'start.willkommen': ['Willkommen!', 'Welcome!'],
    'start.einleitung': ['Melden Sie Ihren Besuch in unter einer Minute an. Wir bestätigen ihn per E-Mail.', 'Register your visit in under a minute. We will confirm it by e-mail.'],
    'start.schritt1': ['1 Anfrage', '1 Request'],
    'start.schritt2': ['2 E-Mail bestätigen', '2 Confirm e-mail'],
    'start.schritt3': ['3 In Prüfung', '3 Under review'],
    'start.antragTitel': ['Besuch anfragen', 'Request a visit'],
    'start.grundBeispiel': ['z. B. Wartung, Termin, Vorstellungsgespräch', 'e.g. maintenance, meeting, interview'],
    'start.senden': ['Anfrage senden', 'Send request'],
    'start.codeTitel': ['E-Mail bestätigen', 'Confirm your e-mail'],
    'start.codeFeld': ['6-stelliger Code', '6-digit code'],
    'start.codeHinweis': ['Ein Bestätigungscode wird in Kürze an {email} gesendet. Er ist 10 Minuten gültig.', 'A confirmation code will be sent to {email} shortly. It is valid for 10 minutes.'],
    'start.codeErneut': ['Code erneut senden', 'Send code again'],
    'start.dankeTitel': ['Vielen Dank!', 'Thank you!'],
    'start.dankeText': ['Ihre E-Mail ist bestätigt und Ihre Anfrage wird jetzt geprüft. Sie erhalten eine E-Mail, sobald sie angenommen oder abgelehnt wurde.', 'Your e-mail is confirmed and your request will now be reviewed. You will receive an e-mail as soon as it has been accepted or rejected.'],
    'start.neu': ['Weiteren Besuch anfragen', 'Request another visit'],
    'start.fehlerSenden': ['Fehler: Die Anfrage konnte nicht gesendet werden.', 'Error: request could not be sent.'],
    'start.codeUnterwegs': ['Ein neuer Code ist unterwegs.', 'A new code is on its way.'],
    'start.codeFalsch': ['Falscher oder abgelaufener Code.', 'Wrong or expired code.'],
    'start.gesperrt': ['Diese E-Mail-Adresse ist wegen zu vieler Anfragen vorübergehend gesperrt.', 'This e-mail address is temporarily blocked because of too many requests.'],

    // Login
    'login.seitentitel': ['EXPass – Anmeldung', 'EXPass – Login'],
    'login.fehler': ['Benutzername oder Passwort ist falsch.', 'Username or password is wrong.'],
    'login.knopf': ['Anmelden', 'Log in'],
    'login.zurueck': ['Zurück zur Startseite', 'Back to start page'],

    // Schaden melden (Mitarbeiter-Seite und Fenster auf der Verwaltungsseite)
    'schaden.seitentitel': ['EXPass – Schaden melden', 'EXPass – Report damage'],
    'schaden.titel': ['Schaden melden', 'Report damage'],
    'schaden.beschreibung': ['Was ist beschädigt? Wo?', 'What is damaged? Where?'],
    'schaden.verursacher': ['Verursacher (nur bei Geständnis)', 'Caused by (only if confessed)'],
    'schaden.senden': ['Meldung senden', 'Send report'],
    'schaden.danke': ['Schaden gemeldet. Vielen Dank!', 'Damage reported. Thank you!'],
    'schaden.gemeldet': ['Schaden gemeldet.', 'Damage reported.'],
    'schaden.fehler': ['Fehler: Der Schaden konnte nicht gemeldet werden.', 'Error: damage could not be reported.'],

    // Verwaltung
    'verwaltung.seitentitel': ['EXPass – Besucherverwaltung', 'EXPass – Visitor Management'],
    'verwaltung.springen': ['Springe zu ▾', 'Jump to ▾'],
    'uebersicht.titel': ['Heute', 'Today'],
    'uebersicht.anwesend': ['Gerade im Haus', 'On site now'],
    'uebersicht.erwartet': ['Besucher heute erwartet', 'Visitors expected today'],
    'uebersicht.schaeden': ['Schäden heute gemeldet', 'Damages reported today'],
    'uebersicht.offen': ['Offene Anfragen', 'Open requests'],
    'verwaltung.hinzufuegenTitel': ['Besucher hinzufügen', 'Add visitor'],
    'verwaltung.antraegeTitel': ['Anfragen', 'Requests'],
    'verwaltung.schaedenTitel': ['Schäden', 'Damages'],
    'verwaltung.besucherTitel': ['Besucher', 'Visitors'],
    'verwaltung.suche': ['Suchen…', 'Search…'],
    'verwaltung.sucheTitel': ['Durchsucht alle Listen. Mehrere Wörter grenzen ein, z. B. ACME 2026-10', 'Searches all lists. Several words narrow the result, e.g. ACME 2026-10'],
    'verwaltung.accountsLink': ['Accounts verwalten', 'Manage accounts'],
    'verwaltung.hinzufuegen': ['Hinzufügen', 'Add'],
    'verwaltung.jetzt': ['Jetzt', 'Now'],
    'verwaltung.ankunftFehlt': ['Ankunftszeit fehlt', 'Arrival time missing'],
    'verwaltung.einchecken': ['Einchecken', 'Check in'],
    'verwaltung.auschecken': ['Auschecken', 'Check out'],
    'verwaltung.fehlerAnkunft': ['Fehler: Die Ankunft konnte nicht gespeichert werden.', 'Error: arrival could not be saved.'],
    'verwaltung.fehlerAustritt': ['Fehler: Der Austritt darf nicht vor der Ankunft liegen.', 'Error: departure must not be before arrival.'],
    'verwaltung.keineRechteBesucher': ['Keine Berechtigung, Besucher zu sehen.', 'No permission to see visitors.'],
    'verwaltung.keineBesucher': ['Noch keine Besucher.', 'No visitors yet.'],
    'verwaltung.besucherHinzugefuegt': ['Besucher hinzugefügt.', 'Visitor added.'],
    'verwaltung.fehlerBesucher': ['Fehler: Der Besucher konnte nicht gespeichert werden.', 'Error: visitor could not be saved.'],
    'verwaltung.keineRechteAntraege': ['Keine Berechtigung, Anfragen zu sehen.', 'No permission to see requests.'],
    'verwaltung.keineAntraege': ['Noch keine Anfragen.', 'No requests yet.'],
    'verwaltung.nurAdmins': ['Nur Admins können Anfragen annehmen oder ablehnen.', 'Only admins can accept or reject requests.'],
    'verwaltung.fehlerEntscheidung': ['Fehler: Die Entscheidung konnte nicht gespeichert werden.', 'Error: decision could not be saved.'],
    'verwaltung.annehmen': ['Annehmen', 'Accept'],
    'verwaltung.ablehnen': ['Ablehnen', 'Reject'],
    'verwaltung.entscheidungOffen': ['Offen', 'Open'],
    'verwaltung.grundOptional': ['Grund (optional)', 'Reason (optional)'],
    'verwaltung.keineRechteSchaeden': ['Keine Berechtigung, Schäden zu sehen.', 'No permission to see damages.'],
    'verwaltung.keineSchaeden': ['Keine Schäden gemeldet.', 'No damages reported.'],

    // Sperrliste (nur Admins)
    'sperrliste.titel': ['Sperrliste', 'Blocklist'],
    'sperrliste.hinweis': ['E-Mail-Adressen, mit denen mehr als 15 Anfragen in 30 Minuten geschickt wurden. 1. Sperre: 24 Stunden, 2.: 1 Monat, 3.: dauerhaft.', 'E-mail addresses that sent more than 15 requests within 30 minutes. 1st block: 24 hours, 2nd: 1 month, 3rd: permanent.'],
    'sperrliste.anzahl': ['Sperren', 'Blocks'],
    'sperrliste.gesperrtAm': ['Gesperrt am', 'Blocked on'],
    'sperrliste.gesperrtBis': ['Gesperrt bis', 'Blocked until'],
    'sperrliste.aufgehoben': ['Aufgehoben von', 'Unblocked by'],
    'sperrliste.gesperrt': ['Gesperrt', 'Blocked'],
    'sperrliste.frei': ['Nicht gesperrt', 'Not blocked'],
    'sperrliste.dauerhaft': ['Dauerhaft', 'Permanent'],
    'sperrliste.aufheben': ['Sperre aufheben', 'Unblock'],
    'sperrliste.aufhebenFrage': ['Sperre für „{email}“ aufheben?', 'Unblock "{email}"?'],
    'sperrliste.aufgehobenOk': ['Sperre aufgehoben.', 'Block removed.'],
    'sperrliste.fehler': ['Fehler: Die Sperre konnte nicht aufgehoben werden.', 'Error: block could not be removed.'],
    'sperrliste.keineRechte': ['Keine Berechtigung, die Sperrliste zu sehen.', 'No permission to see the blocklist.'],
    'sperrliste.leer': ['Keine gesperrten E-Mail-Adressen.', 'No blocked e-mail addresses.'],

    // Accounts
    'accounts.seitentitel': ['EXPass – Accounts', 'EXPass – Accounts'],
    'accounts.zurueck': ['Zurück zur Besucherverwaltung', 'Back to visitor management'],
    'accounts.erstellenTitel': ['Account erstellen', 'Create account'],
    'accounts.passwort': ['Passwort (mind. 8 Zeichen)', 'Password (min. 8 characters)'],
    'accounts.adminCheckbox': ['Admin (nur für Verwaltung)', 'Admin (only for administration)'],
    'accounts.erstellen': ['Erstellen', 'Create'],
    'accounts.alleTitel': ['Alle Accounts', 'All accounts'],
    'accounts.suche': ['Accounts suchen…', 'Search accounts…'],
    'accounts.sucheTitel': ['Durchsucht Benutzername, Rolle und Admin. Mehrere Wörter grenzen ein, z. B. admin ja', 'Searches username, role and admin. Several words narrow the result, e.g. admin yes'],
    'accounts.du': ['(du)', '(you)'],
    'accounts.adminEntziehen': ['Admin entziehen', 'Remove admin'],
    'accounts.adminGeben': ['Zum Admin machen', 'Make admin'],
    'accounts.loeschen': ['Löschen', 'Delete'],
    'accounts.loeschenFrage': ['Account „{name}“ löschen?', 'Delete account "{name}"?'],
    'accounts.keineRechte': ['Keine Berechtigung, Accounts zu sehen.', 'No permission to see accounts.'],
    'accounts.erstellt': ['Account erstellt.', 'Account created.'],
    'accounts.fehler': ['Fehler: {text}', 'Error: {text}'],
};

// Fehlermeldungen vom Server (AccountResource): Schlüssel ist der englische Text,
// auf Englisch wird er unverändert angezeigt
const SERVER_TEXTE = {
    'Username is missing.': 'Benutzername fehlt.',
    'Password needs at least 8 characters.': 'Das Passwort braucht mindestens 8 Zeichen.',
    'Role is missing.': 'Rolle fehlt.',
    'Username already exists.': 'Den Benutzernamen gibt es schon.',
    'You cannot change your own account.': 'Den eigenen Account kann man nicht ändern.',
    'Account not found.': 'Account nicht gefunden.',
    'Only administration accounts can be admin.': 'Nur Verwaltungs-Accounts können Admin sein.',
};

const SPRACHEN = ['de', 'en'];

// Gespeicherte Wahl, sonst die Sprache des Browsers (Deutsch oder sonst Englisch)
function startSprache() {
    try {
        const gespeichert = localStorage.getItem('sprache');
        if (SPRACHEN.includes(gespeichert)) {
            return gespeichert;
        }
    } catch {
        // z. B. privates Fenster ohne Speicher – dann eben Browsersprache
    }
    return (navigator.language || '').toLowerCase().startsWith('de') ? 'de' : 'en';
}

let sprache = startSprache();

// Text zum Schlüssel in der aktuellen Sprache; {name} wird durch werte.name ersetzt
function t(schluessel, werte = {}) {
    const text = TEXTE[schluessel]?.[SPRACHEN.indexOf(sprache)] ?? schluessel;
    return text.replace(/\{(\w+)\}/g, (_, name) => werte[name] ?? '');
}

// Fehlermeldung vom Server übersetzen, falls es eine Übersetzung gibt
function tServer(text) {
    return sprache === 'de' ? SERVER_TEXTE[text] ?? text : text;
}

function uebersetzeSeite() {
    document.documentElement.lang = sprache;
    document.querySelectorAll('[data-i18n]').forEach(element => {
        element.textContent = t(element.dataset.i18n);
    });
    document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
        element.placeholder = t(element.dataset.i18nPlaceholder);
    });
    document.querySelectorAll('[data-i18n-title]').forEach(element => {
        element.title = t(element.dataset.i18nTitle);
    });
    document.querySelectorAll('[data-i18n-aria-label]').forEach(element => {
        element.setAttribute('aria-label', t(element.dataset.i18nAriaLabel));
    });
    document.querySelectorAll('.sprachwahl button').forEach(knopf => {
        knopf.classList.toggle('aktiv', knopf.dataset.sprache === sprache);
    });
}

function setzeSprache(neu) {
    sprache = neu;
    try {
        localStorage.setItem('sprache', neu);
    } catch {
        // Speichern nicht möglich – gilt dann nur für diese Seite
    }
    uebersetzeSeite();
    document.dispatchEvent(new Event('sprachwechsel'));
}

// Knöpfe DE | EN in jedes Element mit der Klasse "sprachwahl" einsetzen
document.querySelectorAll('.sprachwahl').forEach(behaelter => {
    behaelter.replaceChildren(...SPRACHEN.map(code => {
        const knopf = document.createElement('button');
        knopf.type = 'button';
        knopf.textContent = code.toUpperCase();
        knopf.dataset.sprache = code;
        knopf.title = code === 'de' ? 'Deutsch' : 'English';
        knopf.addEventListener('click', () => setzeSprache(code));
        return knopf;
    }));
});

uebersetzeSeite();
