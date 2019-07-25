package com.progmatic.recordislandbackend.dao;

import com.progmatic.recordislandbackend.domain.User;
import com.progmatic.recordislandbackend.domain.VerificationToken;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    
    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("DELETE FROM VerificationToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);
}
