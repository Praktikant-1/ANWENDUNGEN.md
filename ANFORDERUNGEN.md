#  Ziel der Anwendung:
   Die manuelle Eingabe von Besucherdaten zu automatisieren.
   Einfache und effizientere Speicherung von Daten.

## Nutzerrollen:
    Besucher: Antrag stellen, E-Mail per Code bestätigen – sonst nichts sehen
    Mitarbeiter: Schäden melden und sich Ein-/Auschecken
    Verwaltung: Heutige Besucher sehen, Schadenslisten/Sperrliste einsehen 
    Verwaltung mit Adminstatus: (CEO/CTO Sekräterin): Wie Verwaltung + Anträge annehmen/ablehnen 
## User-Stories
- Als CEO (Verwaltung) möchte ich die heutigen Besucher einsehen um zu wissen wer heute erwartet wird.
- Als Techniker (Besucher) möchte ich mich Anmelden um eine defekte Leitung zu reparieren.
- Als Mitarbeiter (Mitarbeiter) möchte ich meine ID scannen um das Gebäude zu betreten.
- Als CEO (Verwaltung) möchte ich einen Nutzer von der IP-Bannliste entfernen damit er erneut einen Antrag kann.
- Als Handwerker (Besucher) möchte ich auschecken um nach Hause zu gehen.
- Als CTO (Verwaltung) möchte ich einen Besucher genehmigen um ihm den Zutritt zu gewähren.
- Als Putzkraft (Besucher) möchte ich einchecken um das Gebäude zu reinigen.
- Als Praktikant (Besucher) möchte ich meinen Qr-Code einscannen um zu lernen.
- Als Besucher (Besucher) möchte ich meine Email verifizieren damit mein Antrag anerkannt wird.

## Daten
| Liste | Felder | Speicherdauer | Begründung |
|---|---|---|---|
| Besuch | Name, E-Mail, Art, Grund, Status, Ein-/Austrittszeit, Bearbeitet von, Genehmigt von | 5 Jahre | mögliche Nachverfolgung von Schäden oder Straftaten |
| Bestätigungscodes/E-mails | Code, E-Mail, Zeitpunkt | 10 min | Verifizierung |
| Schäden | Beschreibung, Datum, Melder, ggf. Verursacher (nur bei Geständnis), Bearbeitet von | 5 Jahre | Nachverfolgung des Tatbestandes und Versicherungsfrage klären |
| Schäden mit Täter | Schaden + verknüpfter Besuch | 5 Jahre | Nachverfolgung des Tatbestandes und Versicherungsfrage klären |
| Sperrliste | IP, Zeitpunkt, Bearbeitet von (bei Aufhebung) | 1. Sperre: 1 Woche, 2. Sperre: 4 Wochen, 3. Sperre: Lifetime | Spamschutz |

    Besuch: mögliche Nachverfolgung von Schäden oder Straftaten
    Bestätigungscodes/ E-mails: Verifizierung
    Schäden: Nachvervolgung des Tatbestandes und Versicherungsfrage klären
    Schäden mit Täter: Nachvervolgung des Tatbestandes und Versicherungsfrage klären
    Sperrliste: Spamblocking

## Nicht-funktionale Anforderungen

### Responsive Design

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

