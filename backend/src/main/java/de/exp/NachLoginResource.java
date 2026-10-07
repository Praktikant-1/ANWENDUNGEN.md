package de.exp;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

// Nach dem Login landet jeder auf seiner eigenen Seite
@Path("/nach-login")
@Authenticated
public class NachLoginResource {

    @Inject
    SecurityIdentity identity;

    @GET
    public Response weiterleiten() {
        String seite = identity.hasRole("verwaltung") ? "/verwaltung.html" : "/mitarbeiter.html";
        return Response.seeOther(URI.create(seite)).build();
    }
}
