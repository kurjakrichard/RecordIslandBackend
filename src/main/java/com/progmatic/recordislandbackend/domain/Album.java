package com.progmatic.recordislandbackend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;

@Entity
@NamedEntityGraphs(
        @NamedEntityGraph(name = "albumsWithSimilarArtists",
                attributeNodes = @NamedAttributeNode(value = "artist", subgraph = "artist.similarArtists"),
                subgraphs = @NamedSubgraph(name = "artist.similarArtists",
                        attributeNodes = @NamedAttributeNode(value = "similarArtists")
                )
        )
)

public class Album implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    @ManyToOne
    private Artist artist;
    private LocalDateTime releaseDate;
    private String img;

    public Album() {
    }

    public Album(String title) {
        this.title = title;
        this.releaseDate = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public int getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }
    
    
}
