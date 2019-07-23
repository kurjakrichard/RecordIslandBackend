package com.progmatic.recordislandbackend.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;import javax.persistence.ManyToMany;
;
import javax.persistence.OneToMany;

@Entity
public class Artist implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique=true)
    private String name;
    @OneToMany(mappedBy="artist")
    private List<Album> albums;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Artist> similarArtists;
    
    public Artist() {
       
    }

    public Artist(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Artist> getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(Set<Artist> similarArtistsForThis) {
        for (Artist similarArtist : similarArtistsForThis) {
            this.similarArtists.add(similarArtist);
        }
    }
    
    
}
