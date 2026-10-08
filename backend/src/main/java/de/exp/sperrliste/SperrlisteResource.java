package de.exp.sperrliste;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

// Die Sperrliste sehen und bearbeiten nur Admins (nicht Verwaltung, nicht Mitarbeiter)
@Path("/sperrliste")
@RolesAllowed("admin")
public class SperrlisteResource {

    // E-Mail im Body statt im Pfad, sonst Ärger mit Zeichen wie + oder /
    public record Aufhebung(String email) {
    }

    @Inject
    SperrlisteService sperrlisteService;

    @Inject
    SecurityIdentity identity;

    @GET
    public List<SperrEintrag> alle() {
        return sperrlisteService.alle();
    }

    @POST
    @Path("/aufheben")
    public SperrEintrag aufheben(Aufhebung aufhebung) {
        if (aufhebung == null || aufhebung.email() == null || aufhebung.email().isBlank()) {
            throw new BadRequestException("E-Mail fehlt");
        }
        SperrEintrag eintrag = sperrlisteService.aufheben(aufhebung.email(), identity.getPrincipal().getName());
        if (eintrag == null) {
            throw new NotFoundException("Diese E-Mail-Adresse ist nicht gesperrt");
        }
        return eintrag;
    }
}
