package de.exp.login;

import de.exp.account.Account;
import de.exp.account.AccountService;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

// Prüft beim Login (Formular) Benutzername und Passwort
@ApplicationScoped
public class AccountIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    AccountService accountService;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest request,
                                              AuthenticationRequestContext context) {
        // Passwort prüfen dauert etwas, deshalb runBlocking
        return context.runBlocking(() -> {
            String passwort = new String(request.getPassword().getPassword());
            Account account = accountService.pruefeLogin(request.getUsername(), passwort);
            if (account == null) {
                throw new AuthenticationFailedException();
            }
            return identityFuer(account);
        });
    }

    static SecurityIdentity identityFuer(Account account) {
        return QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal(account.getBenutzername()))
                .addRoles(account.getRollen())
                .build();
    }
}
