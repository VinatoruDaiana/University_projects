package com.disi.backend.repository;

import com.disi.backend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // Custom query to join User and UserProfile, filter out admins/banned, and search via LIKE
    @Query("SELECT u, p FROM User u LEFT JOIN UserProfile p ON u.userId = p.userId " +
            "WHERE u.isBanned = false AND u.role != 'ADMIN' " +
            "AND u.email != :currentUserEmail " +
            "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Object[]> searchUsersByQuery(
            @Param("query") String query,
            @Param("currentUserEmail") String currentUserEmail,
            Pageable pageable
    );
}
