package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer>{
    
    Authority findByName(String name);
}
