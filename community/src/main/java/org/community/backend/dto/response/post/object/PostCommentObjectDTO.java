package org.community.backend.dto.response.post.object;


import lombok.Getter;
import org.community.backend.domain.entity.PostComment;

import java.time.LocalDateTime;

@Getter
public class PostCommentObjectDTO {
    private String comment_writer;
    private Long comment_num;
    private LocalDateTime comment_date;
    private String comment_content;

    public PostCommentObjectDTO(PostComment postComment, String commentWriter) {
        this.comment_writer = commentWriter;
        this.comment_num = postComment.getId();
        this.comment_date = postComment.getCreatedAt();
        this.comment_content = postComment.getContent();
    }
}