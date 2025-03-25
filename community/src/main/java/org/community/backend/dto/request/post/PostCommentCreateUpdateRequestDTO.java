package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCommentCreateUpdateRequestDTO {

    @NotNull
    private Long user_id;

    @NotBlank
    private String comment_content;
}
