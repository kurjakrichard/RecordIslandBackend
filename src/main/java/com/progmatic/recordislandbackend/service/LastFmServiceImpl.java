package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.config.RecordIslandProperties;
import com.progmatic.recordislandbackend.dto.SimilarArtistsWrapperDTO;
import com.progmatic.recordislandbackend.dto.TagsWrapperDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LastFmServiceImpl {
    
    private RecordIslandProperties properties;

    @Autowired
    public LastFmServiceImpl(RecordIslandProperties properties) {
        this.properties = properties;
    }
    

    public List<String> listGenres() {
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity request = new HttpEntity(requestHeaders);
        ResponseEntity<TagsWrapperDTO> response = rt.exchange("http://ws.audioscrobbler.com/2.0/?method=chart.gettoptags&api_key=b5d3a2c3ed3d171de8652c3aea4e170e&format=json",
                HttpMethod.GET,
                request,
                TagsWrapperDTO.class);

        TagsWrapperDTO tags = response.getBody();
        List<String> genres = tags.getTags().getTag().stream().map(t -> t.getName()).collect(Collectors.toList());
        
        return genres;
    }
    
    public List<String> listSimilarArtists(String name){
        RestTemplate rt = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host("ws.audioscrobbler.com")
                .path("/2.0/").queryParam("method", "artist.getsimilar&artist").queryParam("artist", name)
                .queryParam("api_key", properties.getLastFmApiKey()).queryParam("format", "json").build();
        
        System.out.println(uriComponents.toUriString());

        HttpEntity request = new HttpEntity(requestHeaders);
        HttpEntity<SimilarArtistsWrapperDTO> response = rt.exchange(uriComponents.toUriString(),
                HttpMethod.GET,
                request,
                SimilarArtistsWrapperDTO.class);

        SimilarArtistsWrapperDTO simartists = response.getBody();
        List<String> result = simartists.getSimilarArtists().getArtists().stream().map(a -> a.getName()).collect(Collectors.toList());
        return result;
    }

}
