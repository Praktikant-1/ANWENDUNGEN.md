package de.exp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

// Hält die Besucherliste, damit sie auch beim Annehmen eines Antrags befüllt werden kann
@ApplicationScoped
public class BesucherService {

    private final List<Besucher> besucherListe = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    public List<Besucher> alle() {
        return besucherListe;
    }

    public Besucher hinzufuegen(Besucher besucher) {
        besucher.setId(naechsteId.getAndIncrement());
        besucher.setAustritt(null);
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
