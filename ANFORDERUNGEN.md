#  Ziel der Anwendung:
   Die manuelle Eingabe von Besucherdaten zu automatisieren.
   Einfache und effizientere Speicherung von Daten.

## Nutzerrollen:
    Besucher: Antrag stellen, E-Mail per Code bestätigen – sonst nichts sehen
    Mitarbeiter: Schäden melden und sich Ein-/Auschecken
    Verwaltung: Heutige Besucher sehen, Schadenslisten/Sperrliste einsehen 
    Verwaltung mit Adminstatus: (CEO/CTO Sekräterin): Wie Verwaltung + Anträge annehmen/ablehnen 
## User-Stories
- Als CEO möchte ich die heutigen Besucher einsehen um zu wissen wer heute erwartet wird.
- Als Techniker möchte ich mich Anmelden um eine defekte Leitung zu reparieren.
- Als Mitarbeiter möchte ich meine ID scannen um das Gebäude zu betreten.
- Als CEO möchte ich einen NUtzer von der IP-Bannliste entfernen damit er erneut einen Antrag kann.
- Als Mitarbeiter möchte ich einen kaputten Stuhl melden um Komfort bei der Arbeit zu gewährleisten.
- Als Handwerker möchte ich auschecken um nach Hause zu gehen.
- Als CTO möchte ich einen Besucher genehmigen um ihm den Zutritt zu gewähren.
- Als Putzkraft möchte ich einchecken um das Gebäude zu reinigen.

## Daten
| Liste | Felder | Speicherdauer | Begründung |
|---|---|---|---|
| Besuch | Name, E-Mail, Art, Grund, Status, Ein-/Austrittszeit, Bearbeitet von, Genehmigt von | 5 Jahre | mögliche Nachverfolgung von Schäden oder Straftaten |
| Bestätigungscodes | Code, E-Mail, Zeitpunkt | 10 min | Verifizierung |
| Schäden | Beschreibung, Datum, Melder, ggf. Verursacher (nur bei Geständnis), Bearbeitet von | 5 Jahre | Nachverfolgung des Tatbestandes und Versicherungsfrage klären |
| Schäden mit Täter | Schaden + verknüpfter Besuch | 5 Jahre | Nachverfolgung des Tatbestandes und Versicherungsfrage klären |
| Sperrliste | IP, Zeitpunkt, Bearbeitet von (bei Aufhebung) | 1. Sperre: 1 Woche, 2. Sperre: 4 Wochen, 3. Sperre: Lifetime | Spamblocking |

    Besuch: mögliche Nachverfolgung von Schäden oder Straftaten
    Bestätigungscodes/ E-mails: Verifizierung
    Schäden: Nachvervolgung des Tatbestandes und Versicherungsfrage klären
    Schäden mit Täter: Nachvervolgung des Tatbestandes und Versicherungsfrage klären
    Sperrliste: Spamblocking

## Nicht-funktionale Anforderungen

### Datenschutz

- Die Speicherdauer der Daten steht im Abschnitt [Daten](#daten).
- Zugriffsrechte:
    - Die **Verwaltung** (CEO, CTO und Sekretärin) hat Admin-Rechte und darf alle Daten sehen.
    - Mitarbeiter sehen nur ihre eigenen Daten.
    - Besucher sehen nur ihre eigenen Daten.
- Besucher werden über die Speicherung ihrer Daten informiert, und zwar über die **Privacy Policy**, die unten auf jeder Seite verlinkt ist.

### Bedienbarkeit

- Die App ist eine Web-App und läuft im Browser, deshalb funktioniert sie auf Handy, Tablet und PC.
- Die Sprache der Oberfläche ist Englisch, kann aber nach Belieben geändert werden.
- Ein Besucher braucht höchstens 1 Minute, um einen Antrag auszufüllen und abzuschicken.

### Browser

- Die App läuft in den aktuellen Versionen aller gängigen Browser. Veraltete Browser werden nicht unterstützt.
- Die App funktioniert auch im Browser auf dem Handy.

### Sicherheit

- Mitarbeiter und Verwaltung müssen sich mit einem Passwort/Account einloggen.
- Eine IP-Adresse kommt auf die Sperrliste, wenn von ihr mehr als 15 Anträge innerhalb von 1 Stunde abgeschickt werden.

## Offene Fragen
   erledigt

