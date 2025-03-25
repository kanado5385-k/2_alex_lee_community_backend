package org.community.backend.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class MemberInfChangeRequestDTO {

    @NotNull
    private Integer user_id;

    @NotBlank
    private String nickname;

    private String profile_image;
}
