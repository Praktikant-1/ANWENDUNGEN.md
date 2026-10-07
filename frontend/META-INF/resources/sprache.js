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

const TEXTE = {
    de: {
        // Allgemein
        'abmelden': 'Abmelden',
        'abbrechen': 'Abbrechen',
        'bestaetigen': 'Bestätigen',
        'ja': 'Ja',
        'nein': 'Nein',
        'treffer.eins': '1 Treffer',
        'treffer.viele': '{n} Treffer',
        'zeit.leer': 'Leer lassen für aktuelle Uhrzeit',

        // Felder und Tabellenspalten
        'feld.name': 'Name',
        'feld.email': 'E-Mail',
        'feld.firma': 'Firma',
        'feld.optional': '(optional)',
        'feld.grund': 'Grund des Besuchs',
        'feld.benutzername': 'Benutzername',
        'feld.passwort': 'Passwort',
        'spalte.grund': 'Grund',
        'spalte.von': 'Von',
        'spalte.bis': 'Bis',
        'spalte.status': 'Status',
        'spalte.entscheidung': 'Entscheidung',
        'spalte.datum': 'Datum',
        'spalte.beschreibung': 'Beschreibung',
        'spalte.gemeldetVon': 'Gemeldet von',
        'spalte.verursacher': 'Verursacher',
        'spalte.ankunft': 'Ankunft',
        'spalte.austritt': 'Austritt',
        'spalte.rolle': 'Rolle',
        'spalte.admin': 'Admin',
        'status.OFFEN': 'Offen',
        'status.ANGENOMMEN': 'Angenommen',
        'status.ABGELEHNT': 'Abgelehnt',
        'rolle.MITARBEITER': 'Mitarbeiter',
        'rolle.VERWALTUNG': 'Verwaltung',

        // Startseite
        'start.login': 'Mitarbeiter-Login',
        'start.willkommen': 'Willkommen!',
        'start.einleitung': 'Melden Sie Ihren Besuch in unter einer Minute an. Wir bestätigen ihn per E-Mail.',
        'start.schritt1': '1 Anfrage',
        'start.schritt2': '2 E-Mail bestätigen',
        'start.schritt3': '3 In Prüfung',
        'start.antragTitel': 'Besuch anfragen',
        'start.grundBeispiel': 'z. B. Wartung, Termin, Vorstellungsgespräch',
        'start.senden': 'Anfrage senden',
        'start.codeTitel': 'E-Mail bestätigen',
        'start.codeFeld': '6-stelliger Code',
        'start.codeHinweis': 'Ein Bestätigungscode wird in Kürze an {email} gesendet. Er ist 10 Minuten gültig.',
        'start.codeErneut': 'Code erneut senden',
        'start.dankeTitel': 'Vielen Dank!',
        'start.dankeText': 'Ihre E-Mail ist bestätigt und Ihre Anfrage wird jetzt geprüft. Sie erhalten eine E-Mail, sobald sie angenommen oder abgelehnt wurde.',
        'start.neu': 'Weiteren Besuch anfragen',
        'start.fehlerSenden': 'Fehler: Die Anfrage konnte nicht gesendet werden.',
        'start.codeUnterwegs': 'Ein neuer Code ist unterwegs.',
        'start.codeFalsch': 'Falscher oder abgelaufener Code.',

        // Login
        'login.seitentitel': 'EXPass – Anmeldung',
        'login.fehler': 'Benutzername oder Passwort ist falsch.',
        'login.knopf': 'Anmelden',
        'login.zurueck': 'Zurück zur Startseite',

        // Schaden melden (Mitarbeiter-Seite und Fenster auf der Verwaltungsseite)
        'schaden.seitentitel': 'EXPass – Schaden melden',
        'schaden.titel': 'Schaden melden',
        'schaden.beschreibung': 'Was ist beschädigt? Wo?',
        'schaden.verursacher': 'Verursacher (nur bei Geständnis)',
        'schaden.senden': 'Meldung senden',
        'schaden.danke': 'Schaden gemeldet. Vielen Dank!',
        'schaden.gemeldet': 'Schaden gemeldet.',
        'schaden.fehler': 'Fehler: Der Schaden konnte nicht gemeldet werden.',

        // Verwaltung
        'verwaltung.seitentitel': 'EXPass – Besucherverwaltung',
        'verwaltung.springen': 'Springe zu ▾',
        'verwaltung.hinzufuegenTitel': 'Besucher hinzufügen',
        'verwaltung.antraegeTitel': 'Anfragen',
        'verwaltung.schaedenTitel': 'Schäden',
        'verwaltung.besucherTitel': 'Besucher',
        'verwaltung.suche': 'Suchen…',
        'verwaltung.sucheTitel': 'Durchsucht alle Listen. Mehrere Wörter grenzen ein, z. B. ACME 2026-10',
        'verwaltung.accountsLink': 'Accounts verwalten',
        'verwaltung.hinzufuegen': 'Hinzufügen',
        'verwaltung.jetzt': 'Jetzt',
        'verwaltung.ankunftFehlt': 'Ankunftszeit fehlt',
        'verwaltung.einchecken': 'Einchecken',
        'verwaltung.auschecken': 'Auschecken',
        'verwaltung.fehlerAnkunft': 'Fehler: Die Ankunft konnte nicht gespeichert werden.',
        'verwaltung.fehlerAustritt': 'Fehler: Der Austritt darf nicht vor der Ankunft liegen.',
        'verwaltung.keineRechteBesucher': 'Keine Berechtigung, Besucher zu sehen.',
        'verwaltung.keineBesucher': 'Noch keine Besucher.',
        'verwaltung.besucherHinzugefuegt': 'Besucher hinzugefügt.',
        'verwaltung.fehlerBesucher': 'Fehler: Der Besucher konnte nicht gespeichert werden.',
        'verwaltung.keineRechteAntraege': 'Keine Berechtigung, Anfragen zu sehen.',
        'verwaltung.keineAntraege': 'Noch keine Anfragen.',
        'verwaltung.nurAdmins': 'Nur Admins können Anfragen annehmen oder ablehnen.',
        'verwaltung.fehlerEntscheidung': 'Fehler: Die Entscheidung konnte nicht gespeichert werden.',
        'verwaltung.annehmen': 'Annehmen',
        'verwaltung.ablehnen': 'Ablehnen',
        'verwaltung.grundOptional': 'Grund (optional)',
        'verwaltung.keineRechteSchaeden': 'Keine Berechtigung, Schäden zu sehen.',
        'verwaltung.keineSchaeden': 'Keine Schäden gemeldet.',

        // Accounts
        'accounts.seitentitel': 'EXPass – Accounts',
        'accounts.zurueck': 'Zurück zur Besucherverwaltung',
        'accounts.erstellenTitel': 'Account erstellen',
        'accounts.passwort': 'Passwort (mind. 8 Zeichen)',
        'accounts.adminCheckbox': 'Admin (nur für Verwaltung)',
        'accounts.erstellen': 'Erstellen',
        'accounts.alleTitel': 'Alle Accounts',
        'accounts.suche': 'Accounts suchen…',
        'accounts.sucheTitel': 'Durchsucht Benutzername, Rolle und Admin. Mehrere Wörter grenzen ein, z. B. admin ja',
        'accounts.du': '(du)',
        'accounts.adminEntziehen': 'Admin entziehen',
        'accounts.adminGeben': 'Zum Admin machen',
        'accounts.loeschen': 'Löschen',
        'accounts.loeschenFrage': 'Account „{name}“ löschen?',
        'accounts.keineRechte': 'Keine Berechtigung, Accounts zu sehen.',
        'accounts.erstellt': 'Account erstellt.',
        'accounts.fehler': 'Fehler: {text}',

        // Fehlermeldungen vom Server (AccountResource) – Schlüssel ist der englische Text
        'server:Username is missing.': 'Benutzername fehlt.',
        'server:Password needs at least 8 characters.': 'Das Passwort braucht mindestens 8 Zeichen.',
        'server:Role is missing.': 'Rolle fehlt.',
        'server:Username already exists.': 'Den Benutzernamen gibt es schon.',
        'server:You cannot change your own account.': 'Den eigenen Account kann man nicht ändern.',
        'server:Account not found.': 'Account nicht gefunden.',
        'server:Only administration accounts can be admin.': 'Nur Verwaltungs-Accounts können Admin sein.',
    },

    en: {
        'abmelden': 'Log out',
        'abbrechen': 'Cancel',
        'bestaetigen': 'Confirm',
        'ja': 'Yes',
        'nein': 'No',
        'treffer.eins': '1 match',
        'treffer.viele': '{n} matches',
        'zeit.leer': 'Leave empty for current time',

        'feld.name': 'Name',
        'feld.email': 'E-mail',
        'feld.firma': 'Company',
        'feld.optional': '(optional)',
        'feld.grund': 'Reason for visit',
        'feld.benutzername': 'Username',
        'feld.passwort': 'Password',
        'spalte.grund': 'Reason',
        'spalte.von': 'From',
        'spalte.bis': 'Until',
        'spalte.status': 'Status',
        'spalte.entscheidung': 'Decision',
        'spalte.datum': 'Date',
        'spalte.beschreibung': 'Description',
        'spalte.gemeldetVon': 'Reported by',
        'spalte.verursacher': 'Caused by',
        'spalte.ankunft': 'Arrival',
        'spalte.austritt': 'Departure',
        'spalte.rolle': 'Role',
        'spalte.admin': 'Admin',
        'status.OFFEN': 'Open',
        'status.ANGENOMMEN': 'Accepted',
        'status.ABGELEHNT': 'Rejected',
        'rolle.MITARBEITER': 'Employee',
        'rolle.VERWALTUNG': 'Administration',

        'start.login': 'Staff login',
        'start.willkommen': 'Welcome!',
        'start.einleitung': 'Register your visit in under a minute. We will confirm it by e-mail.',
        'start.schritt1': '1 Request',
        'start.schritt2': '2 Confirm e-mail',
        'start.schritt3': '3 Under review',
        'start.antragTitel': 'Request a visit',
        'start.grundBeispiel': 'e.g. maintenance, meeting, interview',
        'start.senden': 'Send request',
        'start.codeTitel': 'Confirm your e-mail',
        'start.codeFeld': '6-digit code',
        'start.codeHinweis': 'A confirmation code will be sent to {email} shortly. It is valid for 10 minutes.',
        'start.codeErneut': 'Send code again',
        'start.dankeTitel': 'Thank you!',
        'start.dankeText': 'Your e-mail is confirmed and your request will now be reviewed. You will receive an e-mail as soon as it has been accepted or rejected.',
        'start.neu': 'Request another visit',
        'start.fehlerSenden': 'Error: request could not be sent.',
        'start.codeUnterwegs': 'A new code is on its way.',
        'start.codeFalsch': 'Wrong or expired code.',

        'login.seitentitel': 'EXPass – Login',
        'login.fehler': 'Username or password is wrong.',
        'login.knopf': 'Log in',
        'login.zurueck': 'Back to start page',

        'schaden.seitentitel': 'EXPass – Report damage',
        'schaden.titel': 'Report damage',
        'schaden.beschreibung': 'What is damaged? Where?',
        'schaden.verursacher': 'Caused by (only if confessed)',
        'schaden.senden': 'Send report',
        'schaden.danke': 'Damage reported. Thank you!',
        'schaden.gemeldet': 'Damage reported.',
        'schaden.fehler': 'Error: damage could not be reported.',

        'verwaltung.seitentitel': 'EXPass – Visitor Management',
        'verwaltung.springen': 'Jump to ▾',
        'verwaltung.hinzufuegenTitel': 'Add visitor',
        'verwaltung.antraegeTitel': 'Requests',
        'verwaltung.schaedenTitel': 'Damages',
        'verwaltung.besucherTitel': 'Visitors',
        'verwaltung.suche': 'Search…',
        'verwaltung.sucheTitel': 'Searches all lists. Several words narrow the result, e.g. ACME 2026-10',
        'verwaltung.accountsLink': 'Manage accounts',
        'verwaltung.hinzufuegen': 'Add',
        'verwaltung.jetzt': 'Now',
        'verwaltung.ankunftFehlt': 'Arrival time missing',
        'verwaltung.einchecken': 'Check in',
        'verwaltung.auschecken': 'Check out',
        'verwaltung.fehlerAnkunft': 'Error: arrival could not be saved.',
        'verwaltung.fehlerAustritt': 'Error: departure must not be before arrival.',
        'verwaltung.keineRechteBesucher': 'No permission to see visitors.',
        'verwaltung.keineBesucher': 'No visitors yet.',
        'verwaltung.besucherHinzugefuegt': 'Visitor added.',
        'verwaltung.fehlerBesucher': 'Error: visitor could not be saved.',
        'verwaltung.keineRechteAntraege': 'No permission to see requests.',
        'verwaltung.keineAntraege': 'No requests yet.',
        'verwaltung.nurAdmins': 'Only admins can accept or reject requests.',
        'verwaltung.fehlerEntscheidung': 'Error: decision could not be saved.',
        'verwaltung.annehmen': 'Accept',
        'verwaltung.ablehnen': 'Reject',
        'verwaltung.grundOptional': 'Reason (optional)',
        'verwaltung.keineRechteSchaeden': 'No permission to see damages.',
        'verwaltung.keineSchaeden': 'No damages reported.',

        'accounts.seitentitel': 'EXPass – Accounts',
        'accounts.zurueck': 'Back to visitor management',
        'accounts.erstellenTitel': 'Create account',
        'accounts.passwort': 'Password (min. 8 characters)',
        'accounts.adminCheckbox': 'Admin (only for administration)',
        'accounts.erstellen': 'Create',
        'accounts.alleTitel': 'All accounts',
        'accounts.suche': 'Search accounts…',
        'accounts.sucheTitel': 'Searches username, role and admin. Several words narrow the result, e.g. admin yes',
        'accounts.du': '(you)',
        'accounts.adminEntziehen': 'Remove admin',
        'accounts.adminGeben': 'Make admin',
        'accounts.loeschen': 'Delete',
        'accounts.loeschenFrage': 'Delete account "{name}"?',
        'accounts.keineRechte': 'No permission to see accounts.',
        'accounts.erstellt': 'Account created.',
        'accounts.fehler': 'Error: {text}',
    },
};

// Gespeicherte Wahl, sonst die Sprache des Browsers (Deutsch oder sonst Englisch)
function startSprache() {
    try {
        const gespeichert = localStorage.getItem('sprache');
        if (gespeichert === 'de' || gespeichert === 'en') {
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
    const text = TEXTE[sprache][schluessel] ?? TEXTE.en[schluessel] ?? schluessel;
    return text.replace(/\{(\w+)\}/g, (_, name) => werte[name] ?? '');
}

// Fehlermeldung vom Server übersetzen, falls es eine Übersetzung gibt
function tServer(text) {
    return TEXTE[sprache]['server:' + text] ?? text;
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
    behaelter.replaceChildren(...['de', 'en'].map(code => {
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
