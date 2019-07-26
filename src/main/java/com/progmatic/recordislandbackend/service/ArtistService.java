package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.dao.ArtistRepository;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.exception.NoSimilarArtistsException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    
    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> getAllArtistsFromDb() {
        return artistRepository.findAll();
    }
    
    @Transactional
    public void addSimilarArtistsToArtist(String name, Set<Artist> artistSet) throws ArtistNotExistsException {
        Artist artist = findArtistByName(name);
        artist.setSimilarArtists(artistSet);
        artistRepository.save(artist);
    }

    public Artist findArtistByName(String name) throws ArtistNotExistsException {
        Artist artist = artistRepository.findByName(name)
                .orElseThrow(() -> new ArtistNotExistsException("Artist does not exist! [ name: " + name + " ]") );
        return artist;
    }
    
    public Artist findArtistById(int id) throws ArtistNotExistsException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow( () -> new ArtistNotExistsException("Artist does not exist! [ id: " + id + " ]"));
        return artist;
    }
    
    public Set<Artist> getSimilarArtistsByIdFromDb(int id) throws NoSimilarArtistsException, ArtistNotExistsException{
        Artist artist = artistRepository.getArtistWithSimilarArtistsById(id)
                .orElseThrow(() -> new ArtistNotExistsException("Artist does not exist! [ id: " + id + " ]"));
        Set<Artist> resultSet = artist.getSimilarArtists();
        return resultSet;
    }
    
    public boolean existsArtistByName(String name){
        return artistRepository.existsByName(name);
    }
}
