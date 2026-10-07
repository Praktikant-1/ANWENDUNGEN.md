// Zeigt eine Meldung als grüne (ok) oder rote (Fehler) Box.
// Erfolgsmeldungen verschwinden nach 4 Sekunden wieder.
function zeigeMeldung(id, text, istFehler) {
    const element = document.getElementById(id);
    element.textContent = text;
    element.className = text ? (istFehler ? 'meldung fehler' : 'meldung ok') : '';
    if (text && !istFehler) {
        setTimeout(() => {
            element.textContent = '';
            element.className = '';
        }, 4000);
    }
}

// Nicht (mehr) eingeloggt: Quarkus leitet auf die Login-Seite um
function zumLoginWennAbgelaufen(antwort) {
    if (antwort.redirected) {
        location.href = '/login/';
        return true;
    }
    return false;
}

// "16:30:00" -> "16:30"
function uhrzeit(wert) {
    return wert ? wert.slice(0, 5) : '';
}

// "2026-10-07T15:10:00" -> "2026-10-07 15:10"
function datumUhrzeit(wert) {
    return wert ? wert.replace('T', ' ').slice(0, 16) : '';
}

// "2026-10-10" bzw. "2026-10-10 – 2026-10-12" bei mehrtägigen Besuchen
function zeitraum(datum, bis) {
    return bis && bis !== datum ? `${datum} – ${bis}` : (datum ?? '');
}

// Zuletzt geladene Listen für die Übersicht (null = noch nicht geladen oder keine Berechtigung)
let letzteBesucher = null;
let letzteAntraege = null;
let letzteSchaeden = null;

// Übersicht oben: offene Anfragen, heute erwartet, gerade im Haus, Schäden heute
function aktualisiereUebersicht() {
    const tag = new Date().toLocaleDateString('sv'); // JJJJ-MM-TT, wie vom Backend
    const zahlen = {
        'zahl-offen': letzteAntraege?.filter(antrag => antrag.status === 'OFFEN').length,
        // Besuch ist heute (mehrtägig: jeder Tag von "datum" bis "bis") und noch nicht eingecheckt
        'zahl-erwartet': letzteBesucher?.filter(b => b.datum <= tag && tag <= (b.bis || b.datum) && !b.ankunft).length,
        // Eingecheckt, aber noch nicht ausgecheckt
        'zahl-im-haus': letzteBesucher?.filter(b => b.ankunft && !b.austritt).length,
        'zahl-schaeden': letzteSchaeden?.filter(schaden => schaden.datum === tag).length,
    };
    for (const [id, zahl] of Object.entries(zahlen)) {
        document.getElementById(id).textContent = zahl ?? '–';
    }
    // Offene Anfragen warten auf eine Entscheidung – deshalb hervorheben
    document.getElementById('kachel-offen').classList.toggle('hervorheben', zahlen['zahl-offen'] > 0);
}

// Ankunftszeit anzeigen oder, wenn noch keine da ist, nachträglich erfassen
function ankunftZelle(besucher) {
    const zelle = document.createElement('td');
    if (besucher.ankunft) {
        zelle.textContent = uhrzeit(besucher.ankunft);
        return zelle;
    }

    // Noch keine Ankunftszeit: rot markieren, damit man sie nicht vergisst
    zelle.classList.add('zeit-fehlt');
    const hinweis = document.createElement('span');
    hinweis.className = 'zeit-fehlt-hinweis';
    hinweis.textContent = t('verwaltung.ankunftFehlt');
    zelle.appendChild(hinweis);

    const feld = document.createElement('input');
    feld.type = 'time';
    feld.title = t('zeit.leer');
    const knopf = document.createElement('button');
    knopf.textContent = t('verwaltung.einchecken');
    knopf.addEventListener('click', async () => {
        const antwort = await fetch(`/besucher/${besucher.id}/ankunft`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(feld.value ? { ankunft: feld.value } : {})
        });
        if (zumLoginWennAbgelaufen(antwort)) return;
        zeigeMeldung('besucher-meldung', antwort.ok ? '' : t('verwaltung.fehlerAnkunft'), !antwort.ok);
        ladeBesucher();
    });
    zelle.append(feld, knopf);
    return zelle;
}

// Austrittszeit anzeigen oder, wenn noch keine da ist, nachträglich erfassen
function austrittZelle(besucher) {
    const zelle = document.createElement('td');
    if (besucher.austritt) {
        zelle.textContent = uhrzeit(besucher.austritt);
        return zelle;
    }
    // Auschecken erst nach dem Einchecken
    if (!besucher.ankunft) {
        return zelle;
    }

    const feld = document.createElement('input');
    feld.type = 'time';
    feld.title = t('zeit.leer');
    const knopf = document.createElement('button');
    knopf.textContent = t('verwaltung.auschecken');
    knopf.addEventListener('click', async () => {
        const antwort = await fetch(`/besucher/${besucher.id}/austritt`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(feld.value ? { austritt: feld.value } : {})
        });
        if (zumLoginWennAbgelaufen(antwort)) return;
        zeigeMeldung('besucher-meldung', antwort.ok ? '' : t('verwaltung.fehlerAustritt'), !antwort.ok);
        ladeBesucher();
    });
    zelle.append(feld, knopf);
    return zelle;
}

async function ladeBesucher() {
    const tabelle = document.getElementById('besucher-tabelle');
    const antwort = await fetch('/besucher');
    if (zumLoginWennAbgelaufen(antwort)) return;
    if (!antwort.ok) {
        tabelle.innerHTML = `<tr><td colspan="6">${t('verwaltung.keineRechteBesucher')}</td></tr>`;
        return;
    }
    const besucherListe = await antwort.json();
    letzteBesucher = besucherListe;
    aktualisiereUebersicht();

    tabelle.innerHTML = '';

    if (besucherListe.length === 0) {
        tabelle.innerHTML = `<tr><td colspan="6">${t('verwaltung.keineBesucher')}</td></tr>`;
        return;
    }

    for (const besucher of besucherListe) {
        const zeile = document.createElement('tr');
        for (const wert of [zeitraum(besucher.datum, besucher.bis), besucher.name, besucher.firma, besucher.grund]) {
            const zelle = document.createElement('td');
            zelle.textContent = wert ?? '';
            zeile.appendChild(zelle);
        }
        zeile.appendChild(ankunftZelle(besucher));
        zeile.appendChild(austrittZelle(besucher));
        tabelle.appendChild(zeile);
    }
}

document.getElementById('besucher-formular').addEventListener('submit', async (event) => {
    event.preventDefault();
    const meldung = document.getElementById('meldung');

    const daten = Object.fromEntries(new FormData(event.target));
    if (!daten.ankunft) delete daten.ankunft;

    const antwort = await fetch('/besucher', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(daten)
    });
    if (zumLoginWennAbgelaufen(antwort)) return;

    if (antwort.ok) {
        zeigeMeldung('meldung', t('verwaltung.besucherHinzugefuegt'), false);
        event.target.reset();
        ladeBesucher();
    } else {
        zeigeMeldung('meldung', t('verwaltung.fehlerBesucher'), true);
    }
});

ladeBesucher();

async function ladeAntraege() {
    const tabelle = document.getElementById('antrag-tabelle');
    const antwort = await fetch('/antraege');
    if (zumLoginWennAbgelaufen(antwort)) return;
    if (!antwort.ok) {
        tabelle.innerHTML = `<tr><td colspan="8">${t('verwaltung.keineRechteAntraege')}</td></tr>`;
        return;
    }
    const antraege = await antwort.json();
    letzteAntraege = antraege;
    aktualisiereUebersicht();

    tabelle.innerHTML = '';

    if (antraege.length === 0) {
        tabelle.innerHTML = `<tr><td colspan="8">${t('verwaltung.keineAntraege')}</td></tr>`;
        return;
    }

    for (const antrag of antraege) {
        const zeile = document.createElement('tr');
        for (const wert of [antrag.name, antrag.email, antrag.firma, antrag.grund, antrag.von, antrag.bis]) {
            const zelle = document.createElement('td');
            zelle.textContent = wert ?? '';
            zeile.appendChild(zelle);
        }

        // Status als farbiges Abzeichen, Ablehngrund dahinter als Text
        const statusZelle = document.createElement('td');
        const badge = document.createElement('span');
        badge.className = 'badge badge-' + antrag.status.toLowerCase();
        badge.textContent = t('status.' + antrag.status);
        statusZelle.appendChild(badge);
        if (antrag.ablehnGrund) {
            statusZelle.append(' ' + antrag.ablehnGrund);
        }
        zeile.appendChild(statusZelle);

        // Offen: Knöpfe zum Entscheiden, sonst wer wann entschieden hat
        const aktionen = document.createElement('td');
        if (antrag.status === 'OFFEN') {
            zeigeEntscheidungsKnoepfe(aktionen, antrag);
        } else if (antrag.entschiedenVon) {
            const zeitpunkt = document.createElement('span');
            zeitpunkt.className = 'entschieden-am';
            zeitpunkt.textContent = datumUhrzeit(antrag.entschiedenAm);
            aktionen.append(antrag.entschiedenVon, document.createElement('br'), zeitpunkt);
        }
        zeile.appendChild(aktionen);
        tabelle.appendChild(zeile);
    }
}

function knopf(text, beimKlick) {
    const element = document.createElement('button');
    element.textContent = text;
    element.addEventListener('click', beimKlick);
    return element;
}

async function entscheiden(antrag, aktion, ablehnGrund) {
    const optionen = { method: 'POST' };
    if (aktion === 'ablehnen') {
        optionen.headers = { 'Content-Type': 'application/json' };
        optionen.body = JSON.stringify({ grund: ablehnGrund });
    }
    const antwort = await fetch(`/antraege/${antrag.id}/${aktion}`, optionen);
    if (zumLoginWennAbgelaufen(antwort)) return;
    const text = antwort.status === 403 ? t('verwaltung.nurAdmins')
        : antwort.ok ? '' : t('verwaltung.fehlerEntscheidung');
    zeigeMeldung('antrag-meldung', text, !antwort.ok);
    ladeAntraege();
}

function zeigeEntscheidungsKnoepfe(aktionen, antrag) {
    aktionen.replaceChildren(
        knopf(t('verwaltung.annehmen'), () => entscheiden(antrag, 'annehmen')),
        knopf(t('verwaltung.ablehnen'), () => zeigeAblehnFeld(aktionen, antrag))
    );
}

// Beim Ablehnen kann ein optionaler Grund angegeben werden, der per Mail an den Besucher geht
function zeigeAblehnFeld(aktionen, antrag) {
    const grund = document.createElement('input');
    grund.placeholder = t('verwaltung.grundOptional');
    grund.maxLength = 500;
    aktionen.replaceChildren(
        grund,
        knopf(t('bestaetigen'), () => entscheiden(antrag, 'ablehnen', grund.value)),
        knopf(t('abbrechen'), () => zeigeEntscheidungsKnoepfe(aktionen, antrag))
    );
    grund.focus();
}

ladeAntraege();

// Link zur Accountverwaltung nur für Admins zeigen
async function zeigeAccountsLink() {
    const antwort = await fetch('/accounts/ich');
    if (antwort.ok && !antwort.redirected && (await antwort.json()).admin) {
        document.getElementById('accounts-link').hidden = false;
    }
}

zeigeAccountsLink();

async function ladeSchaeden() {
    const tabelle = document.getElementById('schaden-tabelle');
    const antwort = await fetch('/schaeden');
    if (zumLoginWennAbgelaufen(antwort)) return;
    if (!antwort.ok) {
        tabelle.innerHTML = `<tr><td colspan="4">${t('verwaltung.keineRechteSchaeden')}</td></tr>`;
        return;
    }
    const schaeden = await antwort.json();
    letzteSchaeden = schaeden;
    aktualisiereUebersicht();

    tabelle.innerHTML = '';
    if (schaeden.length === 0) {
        tabelle.innerHTML = `<tr><td colspan="4">${t('verwaltung.keineSchaeden')}</td></tr>`;
        return;
    }

    for (const schaden of schaeden) {
        const zeile = document.createElement('tr');
        for (const wert of [schaden.datum, schaden.beschreibung, schaden.melder, schaden.verursacher]) {
            const zelle = document.createElement('td');
            zelle.textContent = wert ?? '';
            zeile.appendChild(zelle);
        }
        tabelle.appendChild(zeile);
    }
}

ladeSchaeden();

document.getElementById('jetzt').addEventListener('click', () => {
    document.querySelector('[name="ankunft"]').value =
        new Date().toTimeString().slice(0, 5);
});

// Datum im Formular mit heute vorbelegen (auch nach dem Zurücksetzen)
const datumFeld = document.querySelector('#besucher-formular [name="datum"]');
const heute = () => new Date().toLocaleDateString('sv');
datumFeld.value = heute();
document.getElementById('besucher-formular').addEventListener('reset', () => {
    setTimeout(() => datumFeld.value = heute());
});

// Sprung-Menü zuklappen, wenn ein Link oder irgendwo daneben geklickt wird
const sprungmenue = document.querySelector('.sprungmenue');
document.addEventListener('click', (event) => {
    if (!sprungmenue.contains(event.target) || event.target.closest('.sprung-links a')) {
        sprungmenue.open = false;
    }
});

// Suche über alle Listen: Jedes Wort muss in irgendeiner Spalte der Zeile vorkommen
const suchFeld = document.getElementById('suche');
const tabellen = ['antrag-tabelle', 'schaden-tabelle', 'besucher-tabelle'].map(id => document.getElementById(id));

function suchtext(zeile) {
    // Zellen mit Eingabefeldern/Knöpfen (Check in, Accept …) zählen nicht mit
    return Array.from(zeile.cells)
        .filter(zelle => !zelle.querySelector('input, button'))
        .map(zelle => zelle.textContent)
        .join(' ')
        .toLowerCase();
}

function sucheAnwenden() {
    const woerter = suchFeld.value.toLowerCase().split(/\s+/).filter(Boolean);
    let treffer = 0;
    for (const tabelle of tabellen) {
        for (const zeile of tabelle.rows) {
            // Hinweiszeilen wie "No visitors yet." immer zeigen
            if (zeile.cells.length === 1) continue;
            const text = suchtext(zeile);
            const passt = woerter.every(wort => text.includes(wort));
            zeile.hidden = !passt;
            if (passt) treffer++;
        }
    }
    document.getElementById('treffer').textContent = woerter.length ? treffer === 1 ? t('treffer.eins') : t('treffer.viele', { n: treffer }) : '';
}

suchFeld.addEventListener('input', sucheAnwenden);
// Listen werden nachgeladen (z. B. nach Accept/Check in) – dann Suche erneut anwenden
const beobachter = new MutationObserver(sucheAnwenden);
tabellen.forEach(tabelle => beobachter.observe(tabelle, { childList: true }));

// Schaden melden (Verwaltung und Admins), öffnet ein Fenster über der Seite
const schadenDialog = document.getElementById('schaden-dialog');
const schadenFormular = document.getElementById('schaden-formular');
const schadenDatum = schadenFormular.querySelector('[name="datum"]');

document.getElementById('schaden-melden').addEventListener('click', () => {
    schadenFormular.reset();
    zeigeMeldung('schaden-dialog-meldung', '', false);
    schadenDatum.max = heute();
    schadenDatum.value = heute();
    schadenDialog.showModal();
});

document.getElementById('schaden-abbrechen').addEventListener('click', () => schadenDialog.close());

schadenFormular.addEventListener('submit', async (event) => {
    event.preventDefault();
    const daten = Object.fromEntries(new FormData(schadenFormular));
    if (!daten.verursacher) delete daten.verursacher;

    const antwort = await fetch('/schaeden', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(daten)
    });
    if (zumLoginWennAbgelaufen(antwort)) return;

    if (antwort.ok) {
        schadenDialog.close();
        zeigeMeldung('schaden-meldung', t('schaden.gemeldet'), false);
        ladeSchaeden();
    } else {
        zeigeMeldung('schaden-dialog-meldung', t('schaden.fehler'), true);
    }
});

// Sprache gewechselt: Tabellen und Trefferzahl in der neuen Sprache neu aufbauen
document.addEventListener('sprachwechsel', () => {
    ladeBesucher();
    ladeAntraege();
    ladeSchaeden();
    sucheAnwenden();
});
