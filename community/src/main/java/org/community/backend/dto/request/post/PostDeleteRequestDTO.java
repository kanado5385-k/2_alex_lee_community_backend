package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostDeleteRequestDTO {

    @NotNull
    private Long user_id;
}
