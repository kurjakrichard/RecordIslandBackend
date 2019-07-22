/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dano
 */
@Service
public class AllMusicWebScrapeService {

    public Set<Album> getAllMusicReleases() {
        final String url = "https://www.allmusic.com/newreleases/all";
        HashSet<Album> releases = new HashSet<>();
        try {
            final Document document = Jsoup.connect(url).maxBodySize(0).get();
//                    System.out.println(document.outerHtml());
            Elements rows = document.select(
                    "table.nr-table tbody tr");
            for (Element row : rows) {
                //in case first table row is empty
                if (row.text().equals("")) {
                    continue;
                } else {
                    if (row.attr("data-type-filter").equals("NEW")) {
                        Album album = new Album();
                        album.setArtist(new Artist(row.select("td:nth-of-type(1)").text()));
                        album.setTitle(row.select("td:nth-of-type(2)").text());
                        releases.add(album);
                    }
                }
            }
//            System.out.println(releases.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return releases;
    }
}
