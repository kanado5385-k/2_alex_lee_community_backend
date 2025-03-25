package org.community.backend.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberPasswordChangeRequestDTO {

    @NotNull
    private Integer user_id;

    @NotBlank
    private String password;
}
