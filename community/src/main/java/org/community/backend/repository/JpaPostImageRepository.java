package org.community.backend.repository;

import org.community.backend.domain.post.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPostImageRepository extends JpaRepository<PostImage, Long> {
    PostImage findByPostId(Long postId);
}
