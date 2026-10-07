package de.exp;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// Wird bei jeder Anfrage mit Login-Cookie aufgerufen.
// So gelten gelöschte Accounts und geänderter Admin-Status sofort.
@ApplicationScoped
public class EingeloggtIdentityProvider implements IdentityProvider<TrustedAuthenticationRequest> {

    @Inject
    AccountService accountService;

    @Override
    public Class<TrustedAuthenticationRequest> getRequestType() {
        return TrustedAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TrustedAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        Account account = accountService.finde(request.getPrincipal());
        if (account == null) {
            return Uni.createFrom().failure(new AuthenticationFailedException());
        }
        return Uni.createFrom().item(AccountIdentityProvider.identityFuer(account));
    }
}
