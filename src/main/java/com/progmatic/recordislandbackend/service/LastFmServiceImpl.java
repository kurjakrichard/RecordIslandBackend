package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.dto.TagsWrapperDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LastFmServiceImpl {

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

}
