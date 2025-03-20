package org.community.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PostComment(Long memberId, Post post, String content) {
        this.memberId = memberId;
        this.post = post;
        this.content = content;
    }

    public void updateComment(PostCommentCreateUpdateRequestDTO postCommentCreateUpdateRequestDTO) {
        this.content = postCommentCreateUpdateRequestDTO.getComment_content();
    }
}
