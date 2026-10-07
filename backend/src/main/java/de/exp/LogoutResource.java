package de.exp;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.FormAuthenticationMechanism;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/logout")
public class LogoutResource {

    @Inject
    SecurityIdentity identity;

    // Löscht das Login-Cookie und schickt zurück zur Startseite
    @POST
    public Response logout() {
        if (!identity.isAnonymous()) {
            FormAuthenticationMechanism.logout(identity);
        }
        return Response.seeOther(URI.create("/")).build();
    }
}
