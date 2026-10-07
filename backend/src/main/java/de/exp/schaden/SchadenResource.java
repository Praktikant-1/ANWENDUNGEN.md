package de.exp.schaden;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Path("/schaeden")
public class SchadenResource {

    private final List<Schaden> schaeden = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    @Inject
    SecurityIdentity identity;

    // Die Verwaltung sieht die Schadensliste
    @GET
    @RolesAllowed("verwaltung")
    public List<Schaden> alle() {
        return schaeden;
    }

    // Mitarbeiter melden Schäden, der Melder ist immer der eingeloggte Benutzer
    @POST
    @RolesAllowed("mitarbeiter")
    public Schaden melden(Schaden schaden) {
        if (schaden.getBeschreibung() == null || schaden.getBeschreibung().isBlank()) {
            throw new BadRequestException("Beschreibung ist Pflicht");
        }
        if (schaden.getDatum() == null) {
            schaden.setDatum(LocalDate.now());
        }
        if (schaden.getDatum().isAfter(LocalDate.now())) {
            throw new BadRequestException("Datum liegt in der Zukunft");
        }
        if (schaden.getVerursacher() != null && schaden.getVerursacher().isBlank()) {
            schaden.setVerursacher(null);
        }
        schaden.setId(naechsteId.getAndIncrement());
        schaden.setMelder(identity.getPrincipal().getName());
        schaeden.add(schaden);
        return schaden;
    }
}
