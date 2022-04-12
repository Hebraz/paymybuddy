package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Authority repository
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    /**
     * Finds authority by authority name
     * @param authority authority name
     * @return an optional of authority
     */
    Optional<Authority> findByAuthority(String authority);
}
