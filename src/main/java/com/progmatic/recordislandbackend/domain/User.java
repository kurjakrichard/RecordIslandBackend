package com.progmatic.recordislandbackend.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "userWithAuthorities",
            attributeNodes = {
                @NamedAttributeNode(value = "authorities")
            })
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String username;
    private String password;
    private LocalDate lastLoginDate;
    @ManyToMany
    private Set<Authority> authorities = new HashSet<>();
    @OneToMany(mappedBy = "user")
    private List<AlbumRating> albumRatings;
    @OneToMany(mappedBy = "user")
    private List<Recommendation> recommendations;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Artist> likedArtists;
    private boolean hasLastFmAccount = false;
    private boolean hasSpotifyAccount = false;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<AlbumRating> getAlbumRatings() {
        return albumRatings;
    }

    public void setAlbumRatings(List<AlbumRating> albumRatings) {
        this.albumRatings = albumRatings;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Artist> getLikedArtists() {
        return likedArtists;
    }

    public void setLikedArtists(Set<Artist> likedArtists) {
        this.likedArtists = likedArtists;
    }
    
    public void addArtistToLikedArtists(Artist artist) {
        this.likedArtists.add(artist);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    public boolean isHasLastFmAccount() {
        return hasLastFmAccount;
    }

    public void setHasLastFmAccountToFalse() {
        this.hasLastFmAccount = false;
    }
    
    public void setHasLastFmAccountToTrue() {
        this.hasLastFmAccount = true;
    }

    public boolean isHasSpotifyAccount() {
        return hasSpotifyAccount;
    }

    public void setHasSpotifyAccountToTrue() {
        this.hasSpotifyAccount = true;
    }
    
    public void setHasSpotifyAccountToFalse() {
        this.hasSpotifyAccount = false;
    }
    
    
}
