package de.exp.besucher;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


@ApplicationScoped
public class BesucherService {

    // Neueste zuerst: nach Datum, dann Ankunftszeit, dann zuletzt angelegt
    private static final Comparator<Besucher> NEUESTE_ZUERST = Comparator
            .comparing(Besucher::getDatum, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Besucher::getAnkunft, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Besucher::getId, Comparator.reverseOrder());

    public List<Besucher> alle() {
        List<Besucher> besucherListe = Besucher.listAll();
        return besucherListe.stream().sorted(NEUESTE_ZUERST).toList();
    }

    @Transactional
    public Besucher hinzufuegen(Besucher besucher) {
        besucher.setId(null);
        besucher.setAustritt(null);
        if (besucher.getDatum() == null) {
            besucher.setDatum(LocalDate.now());
        }
        besucher.persist();
        return besucher;
    }

    public Besucher finde(long id) {
        Besucher besucher = Besucher.findById(id);
        if (besucher == null) {
            throw new NotFoundException();
        }
        return besucher;
    }
}
