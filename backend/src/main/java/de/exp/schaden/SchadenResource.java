package de.exp.schaden;

import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.time.LocalDate;
import java.util.List;

@Path("/schaeden")
public class SchadenResource {

    @Inject
    SecurityIdentity identity;

    @GET
    @RolesAllowed("verwaltung")
    public List<Schaden> alle() {
        return Schaden.listAll(Sort.by("id"));
    }

    @POST
    @RolesAllowed({"mitarbeiter", "verwaltung"})
    @Transactional
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

        schaden.setId(null);
        schaden.setMelder(identity.getPrincipal().getName());
        schaden.persist();
        return schaden;
    }
}
