package de.exp.antrag;

import de.exp.besucher.Besucher;
import de.exp.besucher.BesucherService;
import de.exp.mail.MailService;
import de.exp.sperrliste.SperrlisteService;
import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Path("/antraege")
public class AntragResource {

    public record CodeAnfrage(String email, String code) {
    }

    public record Ablehnung(String grund) {
    }

    static final int MAX_LAENGE_ABLEHNGRUND = 500;

    @Inject
    MailService mailService;

    @Inject
    BesucherService besucherService;

    @Inject
    SperrlisteService sperrlisteService;

    @Inject
    SecurityIdentity identity;

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
        if (!sperrlisteService.versuchErlaubt(antrag.getEmail())) {
            throw new ForbiddenException("E-Mail-Adresse ist gesperrt");
        }
        antrag.setId(naechsteId.getAndIncrement());
        antrag.setEmail(MailService.normalisieren(antrag.getEmail()));
        antrag.setStatus(Antrag.Status.UNBESTAETIGT);
        antrag.setAblehnGrund(null);
        antrag.setEntschiedenVon(null);
        antrag.setEntschiedenAm(null);
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
                && hatUnbestaetigte(MailService.normalisieren(anfrage.email()))
                && sperrlisteService.versuchErlaubt(anfrage.email())) {
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
    public Antrag ablehnen(@PathParam("id") long id, Ablehnung ablehnung) {
        String grund = ablehnung == null || leer(ablehnung.grund()) ? null : ablehnung.grund().trim();
        if (grund != null && grund.length() > MAX_LAENGE_ABLEHNGRUND) {
            throw new BadRequestException("Ablehngrund ist zu lang (max. " + MAX_LAENGE_ABLEHNGRUND + " Zeichen)");
        }
        return entscheiden(id, Antrag.Status.ABGELEHNT, grund);
    }

    private Antrag entscheiden(long id, Antrag.Status status, String ablehnGrund) {
        Antrag antrag = antraege.stream()
                .filter(a -> a.getId() == id && a.getStatus() != Antrag.Status.UNBESTAETIGT)
                .findFirst()
                .orElseThrow(NotFoundException::new);
        // synchronized: klicken zwei Admins gleichzeitig, gewinnt nur einer
        synchronized (antrag) {
            if (antrag.getStatus() != Antrag.Status.OFFEN) {
                throw new ClientErrorException("Antrag wurde bereits entschieden", Response.Status.CONFLICT);
            }
            antrag.setStatus(status);
            antrag.setAblehnGrund(ablehnGrund);
            antrag.setEntschiedenVon(identity.getPrincipal().getName());
            antrag.setEntschiedenAm(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        }
        if (status == Antrag.Status.ANGENOMMEN) {
            besucherService.hinzufuegen(alsBesucher(antrag));
        }
        try {
            mailService.entscheidungSenden(antrag);
        } catch (RuntimeException e) {
            // Die Entscheidung bleibt gültig, auch wenn die Mail nicht rausgeht
            Log.errorf(e, "Entscheidungs-Mail für Antrag %d konnte nicht gesendet werden", id);
        }
        return antrag;
    }

    // Ankunftszeit bleibt leer, bis der Besucher tatsächlich da ist
    private static Besucher alsBesucher(Antrag antrag) {
        Besucher besucher = new Besucher();
        besucher.setName(antrag.getName());
        besucher.setFirma(antrag.getFirma());
        besucher.setGrund(antrag.getGrund());
        besucher.setDatum(antrag.getVon());
        besucher.setBis(antrag.getBis());
        return besucher;
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
