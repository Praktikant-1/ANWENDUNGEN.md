package de.exp.besucher;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Path("/besucher")
public class BesucherResource {

    @Inject
    BesucherService besucherService;

    @GET
    @RolesAllowed("verwaltung")
    public List<Besucher> alle() {
        return besucherService.alle();
    }

    @POST
    @RolesAllowed("verwaltung")
    public Besucher hinzufuegen(Besucher besucher) {
        return besucherService.hinzufuegen(besucher);
    }

    @POST
    @Path("/{id}/ankunft")
    @RolesAllowed("verwaltung")
    @Transactional
    public Besucher ankunftErfassen(@PathParam("id") long id, Besucher daten) {
        Besucher besucher = besucherService.finde(id);
        if (besucher.getAnkunft() != null) {
            throw new ClientErrorException("Ankunft ist bereits erfasst", Response.Status.CONFLICT);
        }
        besucher.setAnkunft(daten != null && daten.getAnkunft() != null
                ? daten.getAnkunft()
                : LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        return besucher;
    }

    @POST
    @Path("/{id}/austritt")
    @RolesAllowed("verwaltung")
    @Transactional
    public Besucher austrittErfassen(@PathParam("id") long id, Besucher daten) {
        Besucher besucher = besucherService.finde(id);

        LocalTime austritt = daten != null && daten.getAustritt() != null
                ? daten.getAustritt()
                : LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        if (besucher.getAnkunft() == null) {
            throw new BadRequestException("Besucher ist noch nicht angekommen");
        }
        if (austritt.isBefore(besucher.getAnkunft())) {
            throw new BadRequestException("Austritt liegt vor der Ankunft");
        }
        besucher.setAustritt(austritt);
        return besucher;
    }
}
