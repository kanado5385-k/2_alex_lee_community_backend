package org.community.backend.dto.response.member;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class MemberInfResponseDTO extends ApiResponse {
    private String nickname;
    private String profileImage;

    public MemberInfResponseDTO(String nickname, String profileImage) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static ResponseEntity<MemberInfResponseDTO> success(String nickname, String profileImage) {
        MemberInfResponseDTO result = new MemberInfResponseDTO(nickname, profileImage);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
