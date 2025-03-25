package org.community.backend.dto.request.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInRequestDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
