package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface UserRepository extends JpaRepository<User, Integer>{
    
    Optional<User> findByEmail(String email);
    
    @EntityGraph(value = "userWithAuthorities")
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> getUserWithAuthoritiesByUsername(@Param("username") String username);
    
    @EntityGraph(value = "userWithAlbumRecommendationsAndLikedArtistsAndDislikedArtists")
    @Query("SELECT u FROM User u WHERE u.username = :name")
    Optional<User> getUserWithRecommendationsAndLikedAndDislikedArtistsByUsername(@Param("name")String username);
    
    boolean existsByUsername(String username);
    
    Optional<User> findByUsername(String name);
    
    @EntityGraph(attributePaths = "likedArtists")
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameWithLikedArtists(String username);
    
    @EntityGraph(value = "userWithAlbumRecommendationsAndLikedArtistsAndDislikedArtistsPastRecommend")
    @Query("SELECT u FROM User u WHERE u.hasNewsLetterSubscription = TRUE")
    List<User> findAllUserWithNewsLetterSubscription();
}
