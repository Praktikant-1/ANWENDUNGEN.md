package de.exp;

import io.quarkus.logging.Log;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Path("/antraege")
public class AntragResource {

    public record CodeAnfrage(String email, String code) {
    }

    @Inject
    MailService mailService;

    private final List<Antrag> antraege = new CopyOnWriteArrayList<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    // Unbestätigte Anträge sieht die Verwaltung nicht
    @GET
    @RolesAllowed("verwaltung")
    public List<Antrag> alle() {
        return antraege.stream()
                .filter(a -> a.getStatus() != Antrag.Status.UNBESTAETIGT)
                .toList();
    }

    // Öffentlich: Besucher stellen hier ihren Antrag
    @POST
    @PermitAll
    public Antrag stellen(Antrag antrag) {
        if (leer(antrag.getName()) || leer(antrag.getGrund())
                || antrag.getVon() == null || antrag.getBis() == null) {
            throw new BadRequestException("Name, Grund und Zeitraum sind Pflicht");
        }
        if (!gueltigeEmail(antrag.getEmail())) {
            throw new BadRequestException("Ungültige E-Mail-Adresse");
        }
        if (antrag.getBis().isBefore(antrag.getVon())) {
            throw new BadRequestException("Enddatum liegt vor dem Startdatum");
        }
        antrag.setId(naechsteId.getAndIncrement());
        antrag.setEmail(MailService.normalisieren(antrag.getEmail()));
        antrag.setStatus(Antrag.Status.UNBESTAETIGT);
        antraege.add(antrag);
        mailService.codeAnfordern(antrag.getEmail());
        return antrag;
    }

    // Öffentlich: Code erneut anfordern. Antwortet immer gleich, damit man nicht
    // herausfinden kann, für welche Adressen Anträge existieren.
    @POST
    @PermitAll
    @Path("/code-senden")
    public Response codeSenden(CodeAnfrage anfrage) {
        if (anfrage != null && gueltigeEmail(anfrage.email())
                && hatUnbestaetigte(MailService.normalisieren(anfrage.email()))) {
            mailService.codeAnfordern(anfrage.email());
        }
        return Response.noContent().build();
    }

    // Öffentlich: bestätigt alle offenen Anträge dieser E-Mail-Adresse
    @POST
    @PermitAll
    @Path("/bestaetigen")
    public Response bestaetigen(CodeAnfrage anfrage) {
        if (anfrage == null || !gueltigeEmail(anfrage.email())
                || !mailService.codePruefen(anfrage.email(), anfrage.code())) {
            throw new BadRequestException("Code ungültig oder abgelaufen");
        }
        String adresse = MailService.normalisieren(anfrage.email());
        antraege.stream()
                .filter(a -> a.getStatus() == Antrag.Status.UNBESTAETIGT && a.getEmail().equals(adresse))
                .forEach(a -> a.setStatus(Antrag.Status.OFFEN));
        return Response.noContent().build();
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/annehmen")
    public Antrag annehmen(@PathParam("id") long id) {
        return entscheiden(id, Antrag.Status.ANGENOMMEN, null);
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/ablehnen")
    public Antrag ablehnen(@PathParam("id") long id) {
        // Den optionalen Ablehngrund hier übergeben, sobald es das Feld gibt
        return entscheiden(id, Antrag.Status.ABGELEHNT, null);
    }

    private Antrag entscheiden(long id, Antrag.Status status, String ablehnGrund) {
        Antrag antrag = antraege.stream()
                .filter(a -> a.getId() == id && a.getStatus() != Antrag.Status.UNBESTAETIGT)
                .findFirst()
                .orElseThrow(NotFoundException::new);
        if (antrag.getStatus() != Antrag.Status.OFFEN) {
            throw new ClientErrorException("Antrag wurde bereits entschieden", Response.Status.CONFLICT);
        }
        antrag.setStatus(status);
        try {
            mailService.entscheidungSenden(antrag, ablehnGrund);
        } catch (RuntimeException e) {
            // Die Entscheidung bleibt gültig, auch wenn die Mail nicht rausgeht
            Log.errorf(e, "Entscheidungs-Mail für Antrag %d konnte nicht gesendet werden", id);
        }
        return antrag;
    }

    private boolean hatUnbestaetigte(String adresse) {
        return antraege.stream()
                .anyMatch(a -> a.getStatus() == Antrag.Status.UNBESTAETIGT && a.getEmail().equals(adresse));
    }

    private static boolean leer(String wert) {
        return wert == null || wert.isBlank();
    }

    private static boolean gueltigeEmail(String email) {
        return email != null && email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
