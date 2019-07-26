package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.Artist;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistRepository extends JpaRepository<Artist, Integer> { //Az Integer az id típusa

    Optional<Artist> findByName(String name);

    @EntityGraph(value = "artistWithSimilarArtists")
    @Query("SELECT art FROM Artist art WHERE art.id = :id")
    Optional<Artist> getArtistWithSimilarArtistsById(@Param("id") int id);
    
    boolean existsByName(String name);
}
