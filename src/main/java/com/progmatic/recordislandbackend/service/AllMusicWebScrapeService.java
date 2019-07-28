package com.progmatic.recordislandbackend.service;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import java.time.LocalDateTime;
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
                        album.setImg(getAlbumImgLink(row.select("td:nth-of-type(2)").select("a").attr("href")));
                        album.setReleaseDate(LocalDateTime.now());
                        releases.add(album);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return releases;
    }

    private String getAlbumImgLink(String url) {
        String imgLink = "";
        try {
            System.out.println(url);
            final Document document = Jsoup.connect(url).maxBodySize(0).get();
            Element div = document.select("div [class=album-contain]").first();
            Element img = div.select("img").first();
            imgLink = img.attr("src");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgLink;
    }
    
    
}
