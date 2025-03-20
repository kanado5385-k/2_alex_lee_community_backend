package org.community.backend.repository;

import org.community.backend.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByPostId(Long postId);
}
