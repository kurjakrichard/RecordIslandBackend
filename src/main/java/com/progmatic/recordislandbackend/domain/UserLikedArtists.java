package com.progmatic.recordislandbackend.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity(name = "UserLikedArtists")
@Table(name = "user_liked_artists")
public class UserLikedArtists implements Serializable {

    @EmbeddedId
    private UserLikedArtistsId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("likedArtistsId")
    private Artist artist;

    private boolean fromLastFm;

    public UserLikedArtists() {
    }

    public UserLikedArtists(User user, Artist artist) {
        this.id = new UserLikedArtistsId(user.getId(), artist.getId());
        this.user = user;
        this.artist = artist;
    }

    public UserLikedArtistsId getId() {
        return id;
    }

    public void setId(UserLikedArtistsId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public boolean isFromLastFm() {
        return fromLastFm;
    }

    public void setFromLastFm(boolean fromLastFm) {
        this.fromLastFm = fromLastFm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserLikedArtists that = (UserLikedArtists) o;
        return Objects.equals(user, that.user)
                && Objects.equals(artist, that.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, artist);
    }
}
