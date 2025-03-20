package org.community.backend.repository;

import org.community.backend.domain.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPostCommentRepository extends JpaRepository<PostComment, Long> {
}
