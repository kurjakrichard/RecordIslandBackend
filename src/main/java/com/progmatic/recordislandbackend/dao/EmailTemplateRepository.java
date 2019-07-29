package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.EmailTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Integer>{
    Optional<EmailTemplate> findByName(String name);
}
