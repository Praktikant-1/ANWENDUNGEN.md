package de.exp.account;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/accounts")
@RolesAllowed("admin")
public class AccountResource {

    @Inject
    AccountService accountService;

    @Inject
    SecurityIdentity identity;

    @GET
    public List<Account> alle() {
        return accountService.alle();
    }

    @GET
    @Path("/ich")
    @Authenticated
    public Map<String, Object> ich() {
        return Map.of(
                "benutzername", identity.getPrincipal().getName(),
                "admin", identity.hasRole("admin"));
    }

    @POST
    public Account erstellen(NeuerAccount neu) {
        if (neu.getBenutzername() == null || neu.getBenutzername().isBlank()) {
            throw fehler(Response.Status.BAD_REQUEST, "Username is missing.");
        }
        if (neu.getPasswort() == null || neu.getPasswort().length() < 8) {
            throw fehler(Response.Status.BAD_REQUEST, "Password needs at least 8 characters.");
        }
        if (neu.getRolle() == null) {
            throw fehler(Response.Status.BAD_REQUEST, "Role is missing.");
        }
        pruefeAdminNurFuerVerwaltung(neu.getRolle(), neu.isAdmin());

        Account account = accountService.erstellen(
                neu.getBenutzername().trim(), neu.getPasswort(), neu.getRolle(), neu.isAdmin());
        if (account == null) {
            throw fehler(Response.Status.CONFLICT, "Username already exists.");
        }
        return account;
    }

    @POST
    @Path("/{benutzername}/admin-geben")
    @Transactional
    public Account adminGeben(@PathParam("benutzername") String benutzername) {
        Account account = findeAnderen(benutzername);
        pruefeAdminNurFuerVerwaltung(account.getRolle(), true);
        account.setAdmin(true);
        return account;
    }

    @POST
    @Path("/{benutzername}/admin-entziehen")
    @Transactional
    public Account adminEntziehen(@PathParam("benutzername") String benutzername) {
        Account account = findeAnderen(benutzername);
        account.setAdmin(false);
        return account;
    }

    @DELETE
    @Path("/{benutzername}")
    public void loeschen(@PathParam("benutzername") String benutzername) {
        findeAnderen(benutzername);
        accountService.loeschen(benutzername);
    }

    private Account findeAnderen(String benutzername) {
        if (benutzername.equals(identity.getPrincipal().getName())) {
            throw fehler(Response.Status.BAD_REQUEST, "You cannot change your own account.");
        }
        Account account = accountService.finde(benutzername);
        if (account == null) {
            throw fehler(Response.Status.NOT_FOUND, "Account not found.");
        }
        return account;
    }

    private void pruefeAdminNurFuerVerwaltung(Account.Rolle rolle, boolean admin) {
        if (admin && rolle != Account.Rolle.VERWALTUNG) {
            throw fehler(Response.Status.BAD_REQUEST, "Only administration accounts can be admin.");
        }
    }

    private static WebApplicationException fehler(Response.Status status, String text) {
        return new WebApplicationException(
                Response.status(status).entity(text).type(MediaType.TEXT_PLAIN).build());
    }
}
