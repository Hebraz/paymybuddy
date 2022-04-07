package com.paymybuddy.application.repository;

import com.paymybuddy.application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User repository
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    /**
     * Get user by email
     * @param email
     * @return optional user
     */
    Optional<User> findByEmail(String email);
}
