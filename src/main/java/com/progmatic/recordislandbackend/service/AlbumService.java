package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.dao.AlbumRepository;
import com.progmatic.recordislandbackend.dao.ArtistRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository,
            ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
    }
    
    public boolean albumExists(String title, String artist) {
        return albumRepository.exists(title, artist);
    }
    
    public Album getAlbumByTitleAndArtist(String title, String artist) throws AlbumNotExistsException{
        Album album = albumRepository.findByTitleAndArtist(title, artist)
                .orElseThrow(() -> new AlbumNotExistsException("[" + title + " - " + artist + "] does not exist!"));
        return album;
    }
    
    public Album findAlbumById(int id) throws AlbumNotExistsException {
        Album album = albumRepository.findById(id)
                .orElseThrow( () -> new AlbumNotExistsException((id + ", id album does not exist!")));
        return album;
    }

    public boolean hasAlbums() {
        return !albumRepository.findAll().isEmpty();
    }
    
    public List<Album> getAllAlbumsWithSimilarArtists() {
        return albumRepository.findAllAlbumsWithSimilarArtists();
    }
    
    public List<Album> getAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(LocalDateTime time) {
        return albumRepository.findAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(time);
    }
    
}
