package de.exp;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Path("/antraege")
public class AntragResource {

    private final List<Antrag> antraege = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    @GET
    @RolesAllowed("verwaltung")
    public List<Antrag> alle() {
        return antraege;
    }

    // Öffentlich: Besucher stellen hier ihren Antrag
    @POST
    @PermitAll
    public Antrag stellen(Antrag antrag) {
        if (antrag.getName() == null || antrag.getName().isBlank()
                || antrag.getVon() == null || antrag.getBis() == null) {
            throw new BadRequestException("Name und Zeitraum sind Pflicht");
        }
        if (antrag.getBis().isBefore(antrag.getVon())) {
            throw new BadRequestException("Enddatum liegt vor dem Startdatum");
        }
        antrag.setId(naechsteId.getAndIncrement());
        antrag.setStatus(Antrag.Status.OFFEN);
        antraege.add(antrag);
        return antrag;
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/annehmen")
    public Antrag annehmen(@PathParam("id") long id) {
        return setzeStatus(id, Antrag.Status.ANGENOMMEN);
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/ablehnen")
    public Antrag ablehnen(@PathParam("id") long id) {
        return setzeStatus(id, Antrag.Status.ABGELEHNT);
    }

    private Antrag setzeStatus(long id, Antrag.Status status) {
        Antrag antrag = antraege.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElseThrow(NotFoundException::new);
        antrag.setStatus(status);
        return antrag;
    }
}
