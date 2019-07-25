package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.dao.AuthorityRepository;
import com.progmatic.recordislandbackend.dao.UserRepository;
import com.progmatic.recordislandbackend.dao.VerificationTokenRepository;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.domain.VerificationToken;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.exception.VerificationTokenNotFoundException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LastFmServiceImpl lastFmService;
    private final VerificationTokenRepository verificationTokenRepository;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    @Autowired
    public UserService(AuthorityRepository authorityRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Lazy LastFmServiceImpl lastFmService,
            VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lastFmService = lastFmService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserWithAuthoritiesByUsername(username);
    }

    @Transactional
    public User createUser(RegistrationDto registration, boolean isAdmin) throws AlreadyExistsException {
        if (userExists(registration.getUsername())) {
            throw new AlreadyExistsException(registration.getUsername() + " is already exists!");
        }
        Authority authority; // TODO: check if it is in authorities if yes, select from db else create;
        if (isAdmin) {
            authority = getAuthorityByName("ROLE_ADMIN");
        } else {
            authority = getAuthorityByName("ROLE_USER");
        }
        User user = new User(registration.getUsername(), passwordEncoder.encode(registration.getPassword()),
                registration.getEmail(), registration.getLastFmUsername(), registration.getSpotifyUsername());
        user.addAuthority(authority);
        userRepository.save(user);
        return user;
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public Authority getAuthorityByName(String name) {
        return authorityRepository.findByName(name);
    }

    @Transactional
    public void updateLastLoginDate(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found! [ " + username + " ]"));
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id( " + id + " )!"));
        return user;
    }

    public User findUserByName(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found! [ " + username + " ]"));
        return user;
    }

    public User getLoggedInUserForTransactions() throws UserNotFoundException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = findUserByName(loggedInUser.getUsername());
        return dbUser;
    }
<<<<<<< Updated upstream
    
    public User getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists() throws UserNotFoundException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EntityGraph eg = em.createEntityGraph("userWithAlbumRecommendationsAndLikedArtistsAndDislikedArtists");
        User dbUser = em.createQuery("SELECT u FROM User u WHERE u.username = :name", User.class)
                .setParameter("name", loggedInUser.getUsername())
                .setHint(HINT_LOADGRAPH, eg)
                .getSingleResult();
        return dbUser;
    }
    
    @Transactional
    public void addUsersLastFmHistory(RegistrationDto registration) {
        if (null != registration.getLastFmUsername() && !registration.getLastFmUsername().isEmpty()) {
            lastFmService.saveLastFmHistory(lastFmService.getLastFmHistory(registration.getLastFmUsername()), registration.getUsername());
        }
    }

    public User getUserFromVerificationToken(final String verificationToken) throws VerificationTokenNotFoundException {
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token not found!"));
        return token.getUser();
    }

    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    public VerificationToken getVerificationToken(final String VerificationToken) throws VerificationTokenNotFoundException {
        VerificationToken token = verificationTokenRepository.findByToken(VerificationToken)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token not found!"));
        return token;
    }

    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) throws VerificationTokenNotFoundException {
        VerificationToken vToken = verificationTokenRepository.findByToken(existingVerificationToken)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token not found!"));
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = verificationTokenRepository.save(vToken);
        return vToken;
    }

    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken; 
        try {
            verificationToken = verificationTokenRepository.findByToken(token).get();
        } catch (NoSuchElementException ex) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                        .getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }
        user.setEnabled(true);
        verificationTokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }
}
