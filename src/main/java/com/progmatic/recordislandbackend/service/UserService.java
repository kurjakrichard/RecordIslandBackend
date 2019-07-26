package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import java.time.LocalDateTime;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import static org.hibernate.jpa.QueryHints.HINT_LOADGRAPH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author balza
 */
@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            EntityGraph eg = em.getEntityGraph("userWithAuthorities");
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .setHint(HINT_LOADGRAPH, eg)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    @Transactional
    public void createUser(RegistrationDto registration) throws AlreadyExistsException {
        if (userExists(registration.getUsername())) {
            throw new AlreadyExistsException(registration.getUsername() + " is already exists!");
        }
        Authority authority = getAuthorityByName("ROLE_USER");
        User user = new User(registration.getUsername(), passwordEncoder.encode(registration.getPassword()),
                registration.getEmail(), registration.getLastFmUsername(), registration.getSpotifyUserName());
        user.addAuthority(authority);
        em.persist(user);
    }

    public boolean userExists(String username) {
        Long num = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return num == 1;
    }

    public Authority getAuthorityByName(String name) {
        return em.createQuery("SELECT a FROM Authority a WHERE a.name = :name", Authority.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    @Transactional
    public void updateLastLoginDate(String username) {
        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
        user.setLastLoginDate(LocalDateTime.now());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String username) {
        User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
        em.remove(user);
    }
}
