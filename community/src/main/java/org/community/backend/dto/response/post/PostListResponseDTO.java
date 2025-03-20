package org.community.backend.dto.response.post;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.community.backend.domain.entity.Post;
import org.community.backend.dto.response.post.object.PostObjectDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostListResponseDTO extends ApiResponse {

    private List<PostObjectDTO> articleList;

    public PostListResponseDTO(List<Post> posts, List<String> writers) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.articleList = posts.stream().map(post -> new PostObjectDTO(post, writers.get(posts.indexOf(post)))).collect(Collectors.toList());
    }

    public static ResponseEntity<PostListResponseDTO> success(List<Post> posts, List<String> writers) {
        PostListResponseDTO result = new PostListResponseDTO(posts, writers);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> noAnyPostFound() {
        ApiResponse response = new ApiResponse(ResponseCode.NO_ANY_POST, ResponseMessage.NO_ANY_POST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

