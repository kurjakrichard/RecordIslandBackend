package com.progmatic.recordislandbackend.domain;

import com.progmatic.recordislandbackend.domain.Album;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AlbumRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Album album;
    private boolean likes;
    private User user;

    public AlbumRating() {
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public boolean isLikes() {
        return likes;
    }

    public void setLikes(boolean likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
