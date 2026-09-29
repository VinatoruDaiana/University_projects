package com.disi.backend.repository;

import com.disi.backend.entity.Photo;
import com.disi.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByUploadedBy(User uploadedBy);
}
