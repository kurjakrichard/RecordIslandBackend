package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.Album;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Integer>{
    
    @Query("SELECT a FROM Album a WHERE a.title = :title AND a.artist.name = :artist")
    boolean exists(@Param("title")String albumTitle, @Param("artist")String artisName);
    
    @Query("SELECT a FROM Album a WHERE a.title = :title AND a.artist.name = :artist")
    Optional<Album> findByTitleAndArtist(@Param("title")String albumTitle, @Param("artist")String artisName);
    
    @EntityGraph(value = "albumsWithSimilarArtists")
    @Query("SELECT alb FROM Album alb")
    List<Album> findAllAlbumsWithSimilarArtists();
    
    @EntityGraph(value = "albumsWithSimilarArtists")
    @Query("SELECT alb FROM Album alb WHERE alb.releaseDate < :time OR alb.releaseDate IS NULL")
    List<Album> findAllAlbumsWithSimilarArtistsReleasedAfterLoggedInUsersLastRecommendationUpdate(@Param("time")LocalDateTime time);
}
