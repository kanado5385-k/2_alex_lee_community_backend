package org.community.backend.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.community.backend.domain.member.Member;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "post_id"})
})
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public PostLike(Long memberId, Post post) {
        this.memberId = memberId;
        this.post = post;
    }
}
