package com.disi.backend.repository;

import com.disi.backend.entity.Album;
import com.disi.backend.entity.Post;
import com.disi.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);
    List<Post> findByAlbum(Album album);

    @Modifying
    @Query("UPDATE Post p SET p.album = null WHERE p.album.albumId = :albumId")
    void unlinkPostsFromAlbum(@Param("albumId") Long albumId);

    @Query("SELECT p FROM Post p WHERE p.user IN :friends ORDER BY p.createdAt DESC")
    Page<Post> findPostsByFriends(@Param("friends") List<User> friends, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user NOT IN :excludedUsers ORDER BY p.createdAt DESC")
    Page<Post> findPostsExcludingUsers(@Param("excludedUsers") List<User> excludedUsers, Pageable pageable);
}
