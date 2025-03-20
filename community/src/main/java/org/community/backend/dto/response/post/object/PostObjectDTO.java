package org.community.backend.dto.response.post.object;

import lombok.Getter;
import org.community.backend.domain.entity.Post;

import java.time.LocalDateTime;

@Getter
public class PostObjectDTO {
    private Long post_num;
    private String post_title;
    private String post_writer;
    private int like_count;
    private int view_count;
    private LocalDateTime post_date;
    private int comment_count;

    public PostObjectDTO(Post post, String postWriter) {
        this.post_num = post.getId();
        this.post_title = post.getTitle();
        this.post_writer = postWriter;
        this.like_count = post.getLikeCount();
        this.view_count = post.getViews();
        this.post_date = post.getCreatedAt();
        this.comment_count = post.getCommentCount();
    }
}