package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.dao.AuthorityRepository;
import com.progmatic.recordislandbackend.dao.PasswordTokenRepository;
import com.progmatic.recordislandbackend.dao.UserRepository;
import com.progmatic.recordislandbackend.dao.VerificationTokenRepository;
import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.domain.PasswordResetToken;
import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.domain.VerificationToken;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.dto.UserProfileEditDTO;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.UserNotFoundException;
import com.progmatic.recordislandbackend.exception.VerificationTokenNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LastFmServiceImpl lastFmService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordTokenRepository passwordTokenRepository;

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(AuthorityRepository authorityRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Lazy LastFmServiceImpl lastFmService,
            VerificationTokenRepository verificationTokenRepository,
            PasswordTokenRepository passwordTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.lastFmService = lastFmService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.authorityRepository = authorityRepository;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found! [ " + username + " ]"));
    }

    @Transactional
    public User createUser(RegistrationDto registration, boolean isAdmin) throws AlreadyExistsException {
        if (userExists(registration.getUsername())) {
            throw new AlreadyExistsException(registration.getUsername() + " is already exists!");
        }
        Authority authority; // TODO: check if it is in authorities if not, create;
        if (isAdmin) {
            authority = getAuthorityByName("ROLE_ADMIN");
        } else {
            authority = getAuthorityByName("ROLE_USER");
        }
        User user = new User(registration.getUsername(), passwordEncoder.encode(registration.getPassword()),
                registration.getEmail(), registration.getLastFmUsername(), registration.hasNewsLetter());
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

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(int id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found! [ ID: " + id + " ]"));
        userRepository.delete(user);
    }

    public User findUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id( " + id + " )!"));
        return user;
    }

    public User findUserByName(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found! [ " + username + " ]"));
        return user;
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email( " + email + " )!"));
        return user;
    }

    public User getLoggedInUserForTransactions() {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = null;
        try {
            dbUser = findUserByName(loggedInUser.getUsername());
        } catch (UserNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        return dbUser;
    }

    public User getLoggedInUserForUpdate() {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = null;
        try {
            dbUser = userRepository.findByUsernameWithLikedArtists(loggedInUser.getUsername()).get();
        } catch (NoSuchElementException ex) {
            logger.error(ex.getMessage() + "User cannot be found in the database!");
        }
        return dbUser;
    }

    public User getLoggedInUserForTransactionsWithRecommendationsAndLikedArtistsAndDislikedArtists() throws UserNotFoundException {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User dbUser = userRepository.getUserWithRecommendationsAndLikedAndDislikedArtistsByUsername(loggedInUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found! [ " + loggedInUser.getUsername() + " ]"));
        return dbUser;
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
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

    public List<User> getAllUserWithNewsLetterSubscription() {
        return userRepository.findAllUserWithNewsLetterSubscription();
    }

    @Transactional
    public void updateUserProfile(UserProfileEditDTO edit) {
        User user = getLoggedInUserForUpdate();
        if (!edit.getLastFmUsername().equals(user.getLastFmAccountName())) {
            user.getLikedArtists().removeIf(artist -> artist.isFromLastFm());
            try {
                lastFmService.saveLastFmHistory(lastFmService.getLastFmHistory(edit.getLastFmUsername()));
            } catch (UserNotFoundException ex) {
                logger.error(ex.getMessage());
            }
        }
        if (null != edit.isHasNewsLetter()) {
            user.setHasNewsLetterSubscription(edit.isHasNewsLetter());
        }
    }

    public PasswordResetToken createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
        return myToken;
    }

    public String validatePasswordResetToken(int id, String token) {
        PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser()
                .getId() != id)) {
            return "invalidToken";
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate()
                .getTime() - cal.getTime()
                        .getTime()) <= 0) {
            passwordTokenRepository.delete(passToken);
            return "expired";
        }

        User user = passToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, Arrays.asList(
                        new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        passwordTokenRepository.delete(passToken);
        return null;
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
