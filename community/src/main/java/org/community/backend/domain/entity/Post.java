package org.community.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor // JPA가 프록시 객체를 셍성하기 위해 반드시 필요 -> 기본생성자 자동으로 생성
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Post(Long memberId, String title, String content) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
    }

    public void incrementViews() {
        this.views++;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }

    public void decrementCommentCount() {
        this.commentCount--;
    }

    public void updatePost(PostCreateUpdateRequestDTO postCreateUpdateRequestDTO) {
        this.title = postCreateUpdateRequestDTO.getPost_title();
        this.content = postCreateUpdateRequestDTO.getPost_content();
    }
}
