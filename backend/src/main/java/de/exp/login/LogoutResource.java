package de.exp.login;

import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.FormAuthenticationMechanism;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;

@Path("/logout")
public class LogoutResource {

    @Inject
    SecurityIdentity identity;

    @POST
    public Response logout() {
        if (identity.getPrincipal() instanceof JsonWebToken) {
            return Response.seeOther(URI.create("/oidc-logout")).build();
        }
        if (!identity.isAnonymous()) {
            FormAuthenticationMechanism.logout(identity);
        }
        return Response.seeOther(URI.create("/")).build();
    }
}
