package com.progmatic.recordislandbackend.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
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
import org.hibernate.annotations.CreationTimestamp;
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
    private LocalDateTime lastLoginDate;
    @CreationTimestamp
    private LocalDateTime createDate;
    @ManyToMany
    private Set<Authority> authorities = new HashSet<>();
    @OneToMany(mappedBy = "user", cascade = {
        CascadeType.ALL })
    private List<AlbumRating> albumRatings;
    @OneToMany(mappedBy = "user", cascade = {
        CascadeType.ALL })
    private List<Recommendation> recommendations;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Artist> likedArtists;
    private String lastFmAccountName;
    private String spotifyAccountName;

    public User() {
        
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastFmAccountName = "";
        this.spotifyAccountName = "";
    }

    public User(String username, String password, String email, String lastFmAccountName, String spotifyAccountName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.lastFmAccountName = lastFmAccountName;
        this.spotifyAccountName = spotifyAccountName;
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

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
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

    public String getLastFmAccountName() {
        return lastFmAccountName;
    }

    public void setLastFmAccountName(String lastFmAccountName) {
        this.lastFmAccountName = lastFmAccountName;
    }

    public String getSpotifyAccountName() {
        return spotifyAccountName;
    }

    public void setSpotifyAccountName(String spotifyAccountName) {
        this.spotifyAccountName = spotifyAccountName;
    }
}
