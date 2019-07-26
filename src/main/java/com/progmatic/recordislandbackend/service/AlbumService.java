package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.dto.AlbumControllerDto;
import com.progmatic.recordislandbackend.exception.AlbumNotExistsException;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.exception.ArtistNotExistsException;
import com.progmatic.recordislandbackend.dao.AlbumRepository;
import com.progmatic.recordislandbackend.dao.ArtistRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository,
            ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }
    
    @Transactional
    public void createAlbum(AlbumControllerDto albumDto) throws AlreadyExistsException, ArtistNotExistsException {

        String artistName = albumDto.getArtistName();
        String albumTitle = albumDto.getTitle();
        if (albumExists(albumDto.getTitle(), artistName)) {
            throw new AlreadyExistsException(albumTitle + " named album is already exists!");
        }
        
        Artist artist = artistRepository.findByName(artistName)
                .orElseThrow(() -> new ArtistNotExistsException(artistName + "named artist does not exist in the database!"));
        
        Album album = new Album(albumTitle, albumDto.getReleaseDate());
        album.setArtist(artist);
        albumRepository.save(album);
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
}
