package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostCreateRequestDTO {

    @NotNull
    private Long user_id;

    @NotBlank
    private String post_title;

    @NotBlank
    private String post_content;

    private String post_image;
}
