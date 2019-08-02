package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Integer>{
    
    PasswordResetToken findByToken(String token);
}
