package com.disi.backend.repository;

import com.disi.backend.entity.Friendship;
import com.disi.backend.entity.FriendshipStatus;
import com.disi.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user OR f.addressee = :user) AND f.status != :status")
    List<Friendship> findActiveOrPendingFriendships(@Param("user") User user, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user1 AND f.addressee = :user2) OR (f.requester = :user2 AND f.addressee = :user1)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Modifying
    @Query("DELETE FROM Friendship f WHERE f.requester = :user OR f.addressee = :user")
    void deleteAllByUser(@Param("user") User user);
}