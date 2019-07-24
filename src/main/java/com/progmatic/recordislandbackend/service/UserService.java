package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.domain.User_;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import java.time.LocalDateTime;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import static org.hibernate.jpa.QueryHints.HINT_LOADGRAPH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

/**
 *
 * @author balza
 */
@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    private PasswordEncoder passwordEncoder;
    private LastFmServiceImpl lastFmService;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, @Lazy LastFmServiceImpl lastFmService) {
        this.passwordEncoder = passwordEncoder;
        this.lastFmService = lastFmService;
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
    public void createUser(RegistrationDto registration, boolean isAdmin) throws AlreadyExistsException {
        if (userExists(registration.getUsername())) {
            throw new AlreadyExistsException(registration.getUsername() + " is already exists!");
        }
        Authority authority;
        if (isAdmin) {
            authority = getAuthorityByName("ROLE_ADMIN");
        } else {
            authority = getAuthorityByName("ROLE_USER");
        }
        User user = new User(registration.getUsername(), passwordEncoder.encode(registration.getPassword()),
                registration.getEmail(), registration.getLastFmUsername(), registration.getSpotifyUsername());
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

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public User findUserById(int id) throws UserNotFoundException {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cQuery = cb.createQuery(User.class);
            Root<User> u = cQuery.from(User.class);
            cQuery.select(u).where(cb.equal(u.get(User_.id), id));

            return em.createQuery(cQuery).getSingleResult();
        } catch (NoResultException ex) {
            throw new UserNotFoundException("User with id " + id + " cannot be found!");
        }
    }

    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public User findUserByName(String name) throws UserNotFoundException {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> cQuery = cb.createQuery(User.class);
            Root<User> u = cQuery.from(User.class);
            cQuery.select(u).where(cb.equal(u.get(User_.username), name));

            return em.createQuery(cQuery).getSingleResult();
        } catch (NoResultException ex) {
            throw new UserNotFoundException("User with name " + name + " cannot be found!");
        }
    }

    public User getLoggedInUser() throws UserNotFoundException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = findUserByName(loggedInUser.getUsername());
        return dbUser;
    }
    
    @Transactional
    public void addUsersLastFmHistory(RegistrationDto registration) {
        if (null != registration.getLastFmUsername() && !registration.getLastFmUsername().isEmpty()) {
            lastFmService.saveLastFmHistory(lastFmService.getLastFmHistory(registration.getLastFmUsername()), registration.getUsername());
        }
    }

}
