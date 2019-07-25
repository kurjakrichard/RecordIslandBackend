package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail(String email);
    
    @EntityGraph(value = "userWithAuthorities")
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUserWithAuthoritiesByUsername(@Param("username") String username);
    
    boolean existsByUsername(String username);
    
    Optional<User> findByUsername(String name);
}
