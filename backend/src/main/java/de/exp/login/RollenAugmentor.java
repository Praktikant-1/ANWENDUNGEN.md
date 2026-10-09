package de.exp.login;

import de.exp.account.Account;
import de.exp.account.AccountService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class RollenAugmentor implements SecurityIdentityAugmentor {

    @Inject
    AccountService accountService;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (!(identity.getPrincipal() instanceof JsonWebToken)) {
            return Uni.createFrom().item(identity);
        }
        return context.runBlocking(() -> {
            String name = identity.getPrincipal().getName();
            Account account = name == null ? null : accountService.findeOhneGrossKlein(name);
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
                    .setPrincipal(identity.getPrincipal())
                    .addCredentials(identity.getCredentials())
                    .addAttributes(identity.getAttributes());
            if (account != null) {
                builder.addRoles(account.getRollen());
            }
            return builder.build();
        });
    }
}
