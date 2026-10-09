package de.exp.antrag;

import de.exp.besucher.Besucher;
import de.exp.besucher.BesucherService;
import de.exp.mail.BesuchsausweisPdf;
import de.exp.mail.MailService;
import de.exp.sperrliste.SperrlisteService;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    BesuchsausweisPdf besuchsausweis;

    @Inject
    SecurityIdentity identity;

    @GET
    @RolesAllowed("verwaltung")
    public List<Antrag> alle() {
        return Antrag.list("status != ?1", Sort.by("id"), Antrag.Status.UNBESTAETIGT);
    }

    @POST
    @PermitAll
    @Transactional
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
        // Eine mitgeschickte id ignorieren, die vergibt die Datenbank
        antrag.setId(null);
        antrag.setEmail(MailService.normalisieren(antrag.getEmail()));
        antrag.setStatus(Antrag.Status.UNBESTAETIGT);
        antrag.setAblehnGrund(null);
        antrag.setEntschiedenVon(null);
        antrag.setEntschiedenAm(null);
        antrag.persist();
        mailService.codeAnfordern(antrag.getEmail());
        return antrag;
    }

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

    @POST
    @PermitAll
    @Path("/bestaetigen")
    @Transactional
    public Response bestaetigen(CodeAnfrage anfrage) {
        if (anfrage == null || !gueltigeEmail(anfrage.email())
                || !mailService.codePruefen(anfrage.email(), anfrage.code())) {
            throw new BadRequestException("Code ungültig oder abgelaufen");
        }
        String adresse = MailService.normalisieren(anfrage.email());
        List<Antrag> unbestaetigte = Antrag.list("status = ?1 and email = ?2", Antrag.Status.UNBESTAETIGT, adresse);
        unbestaetigte.forEach(a -> {
            a.setStatus(Antrag.Status.OFFEN);
            try {
                mailService.neuerAntragMelden(a);
            } catch (RuntimeException e) {
                // Der Antrag bleibt bestätigt, auch wenn die Mail nicht rausgeht
                Log.errorf(e, "Benachrichtigung für Antrag %d konnte nicht gesendet werden", a.getId());
            }
        });
        return Response.noContent().build();
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/annehmen")
    @Transactional
    public Antrag annehmen(@PathParam("id") long id) {
        return entscheiden(id, Antrag.Status.ANGENOMMEN, null);
    }

    @POST
    @RolesAllowed("admin")
    @Path("/{id}/ablehnen")
    @Transactional
    public Antrag ablehnen(@PathParam("id") long id, Ablehnung ablehnung) {
        String grund = ablehnung == null || leer(ablehnung.grund()) ? null : ablehnung.grund().trim();
        if (grund != null && grund.length() > MAX_LAENGE_ABLEHNGRUND) {
            throw new BadRequestException("Ablehngrund ist zu lang (max. " + MAX_LAENGE_ABLEHNGRUND + " Zeichen)");
        }
        return entscheiden(id, Antrag.Status.ABGELEHNT, grund);
    }

    @GET
    @RolesAllowed("verwaltung")
    @Path("/{id}/ausweis")
    @Produces("application/pdf")
    public Response ausweis(@PathParam("id") long id) {
        Antrag antrag = Antrag.findById(id);
        if (antrag == null || antrag.getStatus() != Antrag.Status.ANGENOMMEN || antrag.getBesucherId() == null) {
            throw new NotFoundException();
        }
        Besucher besucher = besucherService.finde(antrag.getBesucherId());
        // "inline": der Browser zeigt die PDF an, statt sie nur herunterzuladen
        return Response.ok(besuchsausweis.erstellen(antrag, besucher.getQrCode()))
                .header("Content-Disposition", "inline; filename=\"visitor-pass-" + id + ".pdf\"")
                .build();
    }

    private Antrag entscheiden(long id, Antrag.Status status, String ablehnGrund) {
        // PESSIMISTIC_WRITE sperrt die Zeile in der Datenbank: Klicken zwei Admins gleichzeitig,
        // wartet der zweite, bis der erste fertig ist, und bekommt dann "bereits entschieden"
        Antrag antrag = Antrag.findById(id, LockModeType.PESSIMISTIC_WRITE);
        if (antrag == null || antrag.getStatus() == Antrag.Status.UNBESTAETIGT) {
            throw new NotFoundException();
        }
        if (antrag.getStatus() != Antrag.Status.OFFEN) {
            throw new ClientErrorException("Antrag wurde bereits entschieden", Response.Status.CONFLICT);
        }
        antrag.setStatus(status);
        antrag.setAblehnGrund(ablehnGrund);
        antrag.setEntschiedenVon(identity.getPrincipal().getName());
        antrag.setEntschiedenAm(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        // Bei Annahme wird ein Besucher angelegt, seinen Ausweis kann die Verwaltung über /{id}/ausweis abrufen
        if (status == Antrag.Status.ANGENOMMEN) {
            Besucher besucher = besucherService.hinzufuegen(alsBesucher(antrag));
            antrag.setBesucherId(besucher.getId());
        }
        try {
            mailService.entscheidungSenden(antrag);
        } catch (RuntimeException e) {
            // Die Entscheidung bleibt gültig, auch wenn die Mail nicht rausgeht
            Log.errorf(e, "Entscheidungs-Mail für Antrag %d konnte nicht gesendet werden", id);
        }
        return antrag;
    }

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
        return Antrag.count("status = ?1 and email = ?2", Antrag.Status.UNBESTAETIGT, adresse) > 0;
    }

    private static boolean leer(String wert) {
        return wert == null || wert.isBlank();
    }

    private static boolean gueltigeEmail(String email) {
        return email != null && email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
