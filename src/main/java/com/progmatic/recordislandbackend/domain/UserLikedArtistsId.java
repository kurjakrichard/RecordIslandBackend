package com.progmatic.recordislandbackend.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/*
Entity for the composite Primary Key
*/

@Embeddable
public class UserLikedArtistsId implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "artist_id")
    private int likedArtistsId;

    private UserLikedArtistsId() {
    }

    public UserLikedArtistsId(int userId, int likedArtistsId) {
        this.userId = userId;
        this.likedArtistsId = likedArtistsId;
    }

    public int getUserId() {
        return userId;
    }

    public int getLikedArtistsId() {
        return likedArtistsId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        UserLikedArtistsId that = (UserLikedArtistsId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(likedArtistsId, that.likedArtistsId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(userId, likedArtistsId);
    }
}
