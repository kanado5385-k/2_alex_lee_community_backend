package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostCommentCreateUpdateRequestDTO {

    @NotNull
    private Long user_id;

    @NotBlank
    private String comment_content;
}
