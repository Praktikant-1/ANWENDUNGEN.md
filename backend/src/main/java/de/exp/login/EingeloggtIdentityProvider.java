package de.exp.login;

import de.exp.account.Account;
import de.exp.account.AccountService;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TrustedAuthenticationRequest;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        // Die Datenbank darf hier nicht direkt benutzt werden, deshalb runBlocking (wie beim Login)
        return context.runBlocking(() -> {
            Account account = accountService.finde(request.getPrincipal());
            if (account == null) {
                throw new AuthenticationFailedException();
            }
            return AccountIdentityProvider.identityFuer(account);
        });
    }
}
