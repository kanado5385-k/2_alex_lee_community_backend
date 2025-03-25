package org.community.backend.dto.request.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDeleteRequestDTO {

    @NotNull
    private Long user_id;
}
