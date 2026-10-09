package de.exp.login;

import io.quarkus.oidc.OidcSession;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/login-oidc")
public class OidcLoginResource {

    @Inject
    SecurityIdentity identity;

    @Inject
    OidcSession oidcSession;

    @GET
    public Uni<Response> weiterleiten() {
        if (identity.getRoles().isEmpty()) {
            return oidcSession.logout()
                    .replaceWith(Response.seeOther(URI.create("/login/?kein-account")).build());
        }
        return Uni.createFrom().item(Response.seeOther(URI.create("/nach-login")).build());
    }
}
