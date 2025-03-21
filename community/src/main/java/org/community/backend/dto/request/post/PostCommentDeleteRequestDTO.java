package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostCommentDeleteRequestDTO {

    @NotNull
    private Long user_id;
}
