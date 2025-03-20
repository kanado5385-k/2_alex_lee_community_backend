package org.community.backend.repository;

import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberIdAndPost(Long memberId, Post post);
}
