package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.dto.AlbumControllerDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.repository.AlbumRepository;
import java.util.List;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author balza
 */
@Service
public class AlbumService {

    @PersistenceContext
    private EntityManager em;

    private AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }
    

    @Transactional
    public void createAlbum(AlbumControllerDto albumDto) throws AlreadyExistsException, ArtistNotExistsException {

        String name = albumDto.getArtistName();
        if (albumExists(albumDto.getTitle(), albumDto.getArtistName())) {
            throw new AlreadyExistsException(albumDto.getTitle());
        }

        if (!artistExists(albumDto.getArtistName())) {
            throw new ArtistNotExistsException(albumDto.getArtistName());
        }
        Artist artist = em.createQuery("SELECT a FROM Artist a WHERE a.name = :name", Artist.class)
                .setParameter("name", name)
                .getSingleResult();

        Album album = new Album(albumDto.getTitle(), albumDto.getReleaseDate());
        album.setArtist(artist);
        em.persist(album);
    }

    public boolean albumExists(String title, String artist) {
        System.out.println(title + " " + artist);
        Long num = em.createQuery("SELECT COUNT(u) FROM Album u WHERE u.title = :title AND u.artist.name = :artist", Long.class)
                .setParameter("title", title)
                .setParameter("artist", artist)
                .getSingleResult();
        return num == 1;
    }

    public boolean artistExists(String name) {
        Long num = em.createQuery("SELECT COUNT(u) FROM Artist u WHERE u.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();

        return num == 1;
    }
    
    public Album albumById(String title, String artist){
        Album album = em.createQuery("SELECT u FROM Album u WHERE u.title = :title AND u.artist.name = :artist", Album.class)
                .setParameter("title", title)
                .setParameter("artist", artist)
                .getSingleResult();
        return album;
    }
    
    public Album findAlbumById(int id) throws AlbumNotExistsException {
        Album album = em.createQuery("SELECT a FROM Album a WHERE a.id = :id", Album.class)
                .setParameter("id", id)
                .getSingleResult();
        return album;
    }

    public boolean hasAlbums() {
        try{
        if(em.createQuery("SELECT COUNT(a) FROM Album a", Long.class).getSingleResult() > Long.valueOf(0))
            return true;
        } catch(NoResultException ex){
            return false;
        }
        return false;
    }
    
    public List<Album> getAllAlbumsFromDb() {
        EntityGraph eg = em.createEntityGraph("albmsWithSimilarArtists");
        return em.createQuery("SELECT alb FROM Album alb")
                .setHint("javax.persistence.fetchgraph", eg)
                .getResultList();
    }
    
    

}
