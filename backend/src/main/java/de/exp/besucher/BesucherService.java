package de.exp.besucher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

// Hält die Besucherliste, damit sie auch beim Annehmen eines Antrags befüllt werden kann
@ApplicationScoped
public class BesucherService {

    private final List<Besucher> besucherListe = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    // Neueste zuerst: nach Datum, dann Ankunftszeit, dann zuletzt angelegt
    private static final Comparator<Besucher> NEUESTE_ZUERST = Comparator
            .comparing(Besucher::getDatum, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Besucher::getAnkunft, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Besucher::getId, Comparator.reverseOrder());

    public List<Besucher> alle() {
        return besucherListe.stream().sorted(NEUESTE_ZUERST).toList();
    }

    public Besucher hinzufuegen(Besucher besucher) {
        besucher.setId(naechsteId.getAndIncrement());
        besucher.setAustritt(null);
        if (besucher.getDatum() == null) {
            besucher.setDatum(LocalDate.now());
        }
        besucherListe.add(besucher);
        return besucher;
    }

    public Besucher finde(long id) {
        return besucherListe.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
