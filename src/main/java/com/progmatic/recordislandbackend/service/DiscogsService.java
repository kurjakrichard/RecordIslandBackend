package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import com.progmatic.recordislandbackend.dto.AlbumDto;
import com.progmatic.recordislandbackend.dto.DiscogsAlbumListDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Dano
 */
@Deprecated
@Service
public class DiscogsService {
    
    private final RecordIslandProperties properties;
    
    @Autowired
    public DiscogsService(RecordIslandProperties properties) {
        this.properties = properties;
    }
    
    public List<Album> getDiscogsPage(int year, int page) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity request = new HttpEntity(requestHeaders);
        
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https").host("api.discogs.com")
                .path("/database/search").queryParam("year", Integer.toString(year))
                .queryParam("format", "album,LP")
                .queryParam("key", properties.getDiscogsApiKey()).queryParam("secret", properties.getDiscogsSecretkey())
                .queryParam("page", page).queryParam("per_page", "100").build();
        System.out.println(uriComponents.toUriString());
        ResponseEntity<DiscogsAlbumListDto> response = rt.exchange(uriComponents.toUriString(),
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
            artist.setName(split[0].trim());
            resultAlbum.setArtist(artist);
            resultAlbum.setTitle(split[1].trim());
        }
        return resultAlbum;
    }
}

