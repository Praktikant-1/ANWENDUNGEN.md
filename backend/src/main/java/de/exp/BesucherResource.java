package de.exp;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Path("/besucher")
public class BesucherResource {

    private final List<Besucher> besucherListe = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    @GET
    @RolesAllowed("verwaltung")
    public List<Besucher> alle() {
        return besucherListe;
    }

    @POST
    @RolesAllowed("verwaltung")
    public Besucher hinzufuegen(Besucher besucher) {
        besucher.setId(naechsteId.getAndIncrement());
        besucher.setAustritt(null);
        besucherListe.add(besucher);
        return besucher;
    }

    // Austrittszeit nachträglich erfassen, z. B. {"austritt": "16:30"}.
    // Ohne Zeit wird die aktuelle Uhrzeit genommen.
    @POST
    @Path("/{id}/austritt")
    @RolesAllowed("verwaltung")
    public Besucher austrittErfassen(@PathParam("id") long id, Besucher daten) {
        Besucher besucher = besucherListe.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);

        LocalTime austritt = daten != null && daten.getAustritt() != null
                ? daten.getAustritt()
                : LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (besucher.getAnkunft() != null && austritt.isBefore(besucher.getAnkunft())) {
            throw new BadRequestException("Austritt liegt vor der Ankunft");
        }
        besucher.setAustritt(austritt);
        return besucher;
    }
}
