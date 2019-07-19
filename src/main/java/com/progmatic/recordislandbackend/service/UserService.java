/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import java.time.LocalDate;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import static org.hibernate.jpa.QueryHints.HINT_LOADGRAPH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            throw new AlreadyExistsException(registration.getUsername());
        }
        Authority authority = getAuthorityByName("ROLE_USER");
        User user = new User(registration.getUsername(), passwordEncoder.encode(registration.getPassword()),
                registration.getEmail());
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
        System.out.println(user.getUsername());
        user.setLastLoginDate(LocalDate.now());
    }

}
