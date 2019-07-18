package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.dto.AlbumDto;
import com.progmatic.recordislandbackend.dto.DiscogsAlbumListDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Dano
 */
@Service
public class DiscogsService {

        public List<Album> getDiscogsPageOne() {
        RestTemplate rt = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity request = new HttpEntity(requestHeaders);
        ResponseEntity<DiscogsAlbumListDto> response = rt.exchange("https://api.discogs.com/database/search?year=2019&format=album,LP&key=zLbHxbaGHKeDBUtHZIJQ&secret=fEkUfEzVftTnhEPoRBTYsdlUeodzRfwy&page=1&per_page=100",
                HttpMethod.GET,
                request,
                DiscogsAlbumListDto.class);
        DiscogsAlbumListDto albumDtos = response.getBody();
        ArrayList<Album> result = new ArrayList<>();
        for (AlbumDto albumDto : albumDtos.getAlbums()) {
            result.add(convertAlbumDtoToAlbum(albumDto));
        }
        return result;
    }
    
        private Album convertAlbumDtoToAlbum(AlbumDto albumDto) {
        Album resultAlbum = new Album();
        
        String[] split = albumDto.getTitle().split("(\\([0-9][0-9]*\\))? - ");
        if (split.length > 0) {
            Artist artist = new Artist();
            artist.setName(split[0]);
            resultAlbum.setArtist(artist);
            resultAlbum.setTitle(split[1]);
        }
        return resultAlbum;
    } 
    }

}
