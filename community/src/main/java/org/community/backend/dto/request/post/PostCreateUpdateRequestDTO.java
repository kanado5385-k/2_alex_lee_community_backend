package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateUpdateRequestDTO {

    @NotNull
    private Long user_id;

    @NotBlank
    private String post_title;

    @NotBlank
    private String post_content;

    private String post_image;
}
