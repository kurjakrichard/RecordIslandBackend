package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.dto.AlbumDto;
import com.progmatic.recordislandbackend.dto.DiscogsAlbumListDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

//    private List<Album> getDiscogsPageOne() {
//        RestTemplate rt = new RestTemplate();
//        HttpHeaders requestHeaders = new HttpHeaders();
//        HttpEntity request = new HttpEntity(requestHeaders);
//        ResponseEntity<DiscogsAlbumListDto> response = rt.exchange("https://api.discogs.com/database/search?year=2019&format=album,LP&key=zLbHxbaGHKeDBUtHZIJQ&secret=fEkUfEzVftTnhEPoRBTYsdlUeodzRfwy&page=1&per_page=100",
//                HttpMethod.GET,
//                request,
//                DiscogsAlbumListDto.class);
//        DiscogsAlbumListDto albums = response.getBody();
//        ArrayList<AlbumDto> result = new ArrayList<>();
//        for (AlbumDto album : albums.getAlbums()) {
//            result.add(album);
//        }
//        return result;
//    }
//    
//    private List<Album> convertAlbumDtoToListAlbumList(List<AlbumDto> dtoList) {
//        ArrayList<Album> resultList = new ArrayList<>();
//        for (AlbumDto albumDto : dtoList) {
//            Album album = new Album();
//            album.setArtist(albumDto.getArtist());
//            album.setTitle(albumDto.getTitle());
//            album.setReleaseDate(albumDto.getReleaseDate());
//        }
//    }

}
