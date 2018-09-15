package coffeeshop.ejb;

import coffeeshop.config.ApplicationConfig;
import coffeeshop.entity.UserInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

@Stateless
public class UserManagerBean implements UserManager {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Pbkdf2PasswordHash passwordHash;
    
    @Inject
    private ApplicationConfig applicationConfig;

    private static final Collection<String> ROLES = new ArrayList<>();

    static {
        ROLES.add("admin");
        ROLES.add("customer");
        ROLES.add("staff");
    }

    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @PostConstruct
    private void init() {
        passwordHash.initialize(applicationConfig.getHashAlgorithmParameters());
    }

    @Override
    public void addUser(String username, String password, String role) {
        if (isUserExisting(username)) {
            throw new EJBException("User already exists");
        } else {
            if (!getRoles().contains(role)) {
                throw new EJBException("Invalid role " + role);
            }
            UserInfo newUser = new UserInfo(null, username, new Date(),
                    passwordHash.generate(password.toCharArray()), role);
            em.persist(newUser);
        }
    }

    @Override
    public void changePassword(String username, String password) {
        // TODO: add changePassword method
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUserExisting(String username) {
        try {
            em.createNamedQuery("User.findByUsername", UserInfo.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @RolesAllowed("admin")
    @Override
    public String getUserRole(String username) throws NoResultException {
        String role = em.createQuery("SELECT u.role FROM UserInfo u WHERE u.username = :username", String.class)
                .setParameter("username", username)
                .getSingleResult();
        return role;
    }

    @Override
    public void verifyPassword(String username, String password) {
        // TODO: add verifyPassword method
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
